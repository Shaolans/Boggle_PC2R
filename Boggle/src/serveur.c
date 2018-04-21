#define _XOPEN_SOURCE 700


/** Serveur TCP **/

#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <sys/select.h>
#include <sys/time.h>
#include <arpa/inet.h>
#include <sys/timerfd.h>
#include <pthread.h>
#include "grille.h"
#include "hashmap.h"


#define MAX_ARG 3
#define MAX_TOURS 2
#define MAX_TAILLE_MSG 10000


typedef struct info_t {
	
	char * userName;
	int score;
	Liste_mot * motsProposes;
	
}Info;

char * grille;
map_t map ;
pthread_mutex_t mutex_map;
Liste_mot * dico;


int verif_mot(char *mot, char *traj){
	
	if( !verif_trajectoire(traj) )
		return -1;
		
	if( !mot_dans_dico(dico, mot))
		return -2;
		
	return 0;
	
	
}


void envoyer_messages_users(map_t map, char* message, int taille){
	
	List_keys *keys, *pkeys;
	int connexion;
	char buffer[1];
	socklen_t len;
	int error = 0;
	int retval ;
	
	keys = hashmap_get_keys(map);
			
	pkeys = keys;
	while(pkeys){
		
		connexion=atoi(pkeys->key);
		if(send(connexion, message, taille, MSG_NOSIGNAL)==-1){
			perror("send");			
		}
		pkeys = pkeys ->next;
	}
		
	list_keys_free(keys);
		
}


char * scores_actuels(map_t map){
	
	
	List_keys *keys, *pkeys;
	Info *pinfo;
	char *scores;
	int taille;
	char scoreJoueur[10];
	
	keys = hashmap_get_keys(map);
	
	scores = malloc(sizeof(char)*100);
	
	sprintf(scores, "%d*", MAX_TOURS);
			
	pkeys = keys;
	while(pkeys){
		
		hashmap_get(map, pkeys->key, &pinfo);
		
		sprintf(scoreJoueur, "%d", pinfo->score);
		
		taille = sizeof(scores)+sizeof(pinfo->userName)+sizeof(scoreJoueur)+2;
		
		memcpy(scores, scores, taille);
		strcat(scores, pinfo->userName);
		strcat(scores, "*");
		strcat(scores, scoreJoueur);
		strcat(scores, "*");
		pkeys = pkeys ->next;
	}
		
	list_keys_free(keys);
	
	return scores;
	
	
}



void * traitement(void *arg){
	
	char buffer[MAX_TAILLE_MSG];
	char *parse;
	char argv[MAX_ARG][20];
	int i = 0;
	char message[MAX_TAILLE_MSG];
	char *userName = malloc(sizeof(char)*20);
	int nb_req=0;
	Info info;
	char *scores;
	int v;
	Liste_mot * lp;
	
	int connexion = *(int *)arg;
	char conn[6];

	while(1){
		i=0;
		sprintf(buffer, "\r\n");
		
		if(recv(connexion, buffer, sizeof(buffer), 0)==-1){
			perror("recv");
			
		}
		
		printf(buffer);
		
		parse = strtok(buffer, "/");
		
		while(parse){
			
			if(i>=MAX_ARG)
				break;
			
			strcpy(argv[i]	, parse);
					
			i++;
			
			parse = strtok(NULL, "/");
			
		}
		
		if(strcmp(argv[0], "CONNEXION")==0){
			memcpy(userName, argv[1], sizeof(argv[1]));
			
			sprintf(message, "CONNECTE/%s/\r\n",userName);
			
			pthread_mutex_lock(&mutex_map);
			envoyer_messages_users(map, message, sizeof(message));
			
			
			info.userName = userName;
			info.score = 0;
			sprintf(conn, "%d", connexion);
			
			
			hashmap_put(map, conn, &info);
			
			scores = scores_actuels(map);
			
			pthread_mutex_unlock(&mutex_map);
			
			sprintf(message, "BIENVENUE/%s/%s/\r\n",grille, scores);
			
			if(send(connexion, message, sizeof(message), 0)==-1){
				perror("send");
				
			}
						
			free(scores);
			
			
		}
		else{
			if(strcmp(argv[0], "SORT")==0){
				
				
				sprintf(message, "DECONNEXION/%s/\r\n", userName);
				
				pthread_mutex_lock(&mutex_map);
				
				hashmap_remove(map, conn);
				
				envoyer_messages_users(map, message, sizeof(message));
				
				pthread_mutex_unlock(&mutex_map);

				break;
			}
			else{
				if(strcmp(argv[0], "TROUVE")==0){
					/*ANALYSE du mot trouve  */
					
					v = verif_mot(argv[1], argv[2] );
					
					if( v == -1 ){
						sprintf(message, "MINVALIDE/POS la trajection est erronee/\r\n");
					}
					else{
						if(v == -2 ){
							sprintf(message, "MINVALIDE/DIC le mot n'apparaitient pas au dico/\r\n");
						}
						else{
							sprintf(message, "MVALIDE/%s/\r\n", argv[1]);
							lp = malloc(sizeof(struct liste_mot));
							memcpy(lp, argv[1], strlen(argv[1]));
							lp -> next = info.motsProposes;
							info.motsProposes = lp;

						}
						
					}
					
					if(send(connexion, message, sizeof(message), 0)==-1){
								perror("send");
					}

				}
				else{
					printf("Requete recue de format inconnu\n");
					printf("%s\n", buffer);
					
					break;
				}
				
			}
		}	
		
	}
	

	

	
	printf("Fin de la connexion pour le serveur %d\n", connexion);
	
	shutdown(connexion, 2);
	free(arg);
	free(userName);
	return NULL;
}


int main(int argc, char **args){
	
	struct sockaddr_in emet, exp;
	fd_set lecteurs, lecteursRef;
	int fds[2];
	int i;
	int connexion;
	int *pconnexion;
	socklen_t fromlen;
	int max1 =0;
	int port;
	struct itimerspec timerValue;
	int timersElapsed ;
	pthread_attr_t attr;
	pthread_t th;
	char message[100];
	int nb_tours=-1;
	
	if(argc != 2){
		printf("Usage : numPort\n");
		return EXIT_FAILURE;
	}
	
	
	dico = charger_dico();
	
	pthread_attr_init(&attr);
	pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
	
	port = atoi(args[1]);
	
	FD_ZERO(&lecteursRef);
	FD_SET(0, &lecteursRef);
					
	memset(&emet, '\0', sizeof(emet));
	emet.sin_family = AF_INET;
	emet.sin_addr.s_addr = htonl(INADDR_ANY); 
	

	emet.sin_port = htons(port);
		
		
	if( (fds[0]=socket(AF_INET, SOCK_STREAM, 0))== -1){
		perror("sock");
		return EXIT_FAILURE;
	}
		
		
	if(bind(fds[0], (struct sockaddr *) &emet, sizeof(emet))==-1){
		perror("bind");
		return EXIT_FAILURE;
	}
		
	FD_SET(fds[0], &lecteursRef);
	if(listen(fds[0], 1)==-1){
		perror("listen");
		return EXIT_FAILURE;
	}
		
	max1 = fds[0];	
	
	if( (fds[1] = timerfd_create(CLOCK_REALTIME, 0)) <0){
		printf("failed to create timer fd\n");
        return EXIT_FAILURE;
	}
	
	FD_SET(fds[1], &lecteursRef);	
	
		
	if(fds[1] > fds[0])
		max1 = fds[1];
		
	max1++;
	
	timerValue.it_value.tv_sec = 10;
    timerValue.it_value.tv_nsec = 0;
    timerValue.it_interval.tv_sec = 10;
    timerValue.it_interval.tv_nsec = 0;
    
    if (timerfd_settime(fds[1], 0, &timerValue, NULL) < 0) {
        printf("could not start timer\n");
        exit(1);
    }
    
    grille = generer_grille();
    map = hashmap_new();
	
	while(1){
		
		
		memcpy(&lecteurs, &lecteursRef, sizeof(lecteursRef));
		
		if(select(max1, &lecteurs, NULL, NULL, NULL)==-1){
			perror("select");
			return EXIT_FAILURE;
		}
		
		if(FD_ISSET(0, &lecteurs))
			break; 
		
		for(i=0; i<2; i++){
			
			if(FD_ISSET(fds[i], &lecteurs)){
				
				if(i==0){
					
					if( (connexion = accept(fds[0], (struct sockaddr *) &exp, &fromlen))==-1){
						perror("accept");
						return EXIT_FAILURE;
					}
					
					pconnexion = malloc(sizeof(int));
					pconnexion[0] = connexion;
					
					if(pthread_create(&th, &attr, traitement, pconnexion)!=0){
						printf("pthread_create\n");
						shutdown(connexion, 2);
						return EXIT_FAILURE;
					}
					
				}
				else{
					
					
					if(nb_tours == -1){
						
						sprintf(message, "SESSION/\r\n");
						pthread_mutex_lock(&mutex_map);
						envoyer_messages_users(map, message, sizeof(message));
						pthread_mutex_unlock(&mutex_map);
						timerValue.it_value.tv_sec = 30;
						timerValue.it_interval.tv_sec = 30;
						
						sprintf(message, "TOUR/%s/\r\n", grille);
						pthread_mutex_lock(&mutex_map);
						
						envoyer_messages_users(map, message, sizeof(message));
						pthread_mutex_unlock(&mutex_map);
						
						
						if (timerfd_settime(fds[1], 0, &timerValue, NULL) < 0) {
							printf("could not start timer\n");
							exit(1);
						}
						
						
					}
					else{
						if(nb_tours == MAX_TOURS){
							/* Phase de resultats */
							
							timerValue.it_value.tv_sec = 30;
							timerValue.it_interval.tv_sec = 30;
						
						
							if (timerfd_settime(fds[1], 0, &timerValue, NULL) < 0) {
								printf("could not start timer\n");
								exit(1);
							}
							
							
							nb_tours = -2;
						}
						else{
							/* Fin de la phase de recherche */
							
							sprintf(message, "RFIN/\r\n");
							pthread_mutex_lock(&mutex_map);
							envoyer_messages_users(map, message, sizeof(message));
							pthread_mutex_unlock(&mutex_map);
							
							detruire_grille(grille);
							grille = generer_grille();
							
							
						}
					}
					
					nb_tours++;
					
					timersElapsed = 0;
					(void) read(fds[1], &timersElapsed, 8);
					printf("timers elapsed: %d\n", timersElapsed);
					
				
						
				}
				
			
			}
			
		}
		
	
	}
	
	
	detruire_grille(grille);
	hashmap_free(map);
	free_dico(dico);
	
	
	pthread_attr_destroy(&attr);
	close(fds[0]);
	close(fds[1]);
	
	return EXIT_SUCCESS;
	
}
