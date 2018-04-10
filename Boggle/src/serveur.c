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

int main(int argc, char **args){
	
	struct sockaddr_in emet, exp;
	fd_set lecteurs, lecteursRef;
	int fds[2];
	int i;
	int connexion;
	socklen_t fromlen;
	int max1 =0;
	int port;
	struct itimerspec timerValue;
	int timersElapsed ;
	
	if(argc != 2){
		printf("Usage : numPort\n");
		return EXIT_FAILURE;
	}
	
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
	
	timerValue.it_value.tv_sec = 1;
    timerValue.it_value.tv_nsec = 0;
    timerValue.it_interval.tv_sec = 1;
    timerValue.it_interval.tv_nsec = 0;
    
    if (timerfd_settime(fds[1], 0, &timerValue, NULL) < 0) {
        printf("could not start timer\n");
        exit(1);
    }
	
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
					printf("ICI CONNEXION !\n");
					if( (connexion = accept(fds[0], (struct sockaddr *) &exp, &fromlen))==-1){
						perror("accept");
						return EXIT_FAILURE;
					}
					shutdown(connexion, 2);
				}
				else{
					printf("ICI TEMPS !\n");
					timersElapsed = 0;
					(void) read(fds[1], &timersElapsed, 8);
					printf("timers elapsed: %d\n", timersElapsed);
						
				}
				
			
			}
			
		}
		
	
	}
	
	
	
	close(fds[0]);
	close(fds[1]);
	
	return EXIT_SUCCESS;
	
}



/*#define _XOPEN_SOURCE 700
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <netinet/in.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <errno.h>
#include <sys/socket.h>
#include <netdb.h>

#define N 10

int main(int argc, char** args){
	
	int port;
	struct sockaddr_in addr;
	struct sockaddr_in exp;
	socklen_t lenexp;
	int sock;
	char nomFic[80];
	int fd;
	int connexion;
	int i=0;
	pthread_t th;
	
	
	if(argc != 2){
		printf("Usage : numPort\n");
		return EXIT_FAILURE;
	}
	
	port = atoi(args[1]);
	
	memset(&addr, '\0', sizeof(addr));
	addr.sin_family = AF_INET;
	addr.sin_addr.s_addr = htonl(INADDR_ANY);
	addr.sin_port = htons(port);
	
	if( (sock = socket(AF_INET, SOCK_STREAM, 0)) == -1){
		perror("socket");
		return EXIT_FAILURE;
	}
	
	if(bind(sock, (struct sockaddr *)&addr, sizeof(addr))==-1){
		perror("bind");
		return EXIT_FAILURE;
	}
	
	if(listen(sock, 1)==-1){
		perror("listen");
		return EXIT_FAILURE;
	}
	
	while(i<2){
		
		if( (connexion=accept(sock, (struct sockaddr *)&exp, &lenexp))==-1){
			perror("accept");
			return EXIT_FAILURE;
		}
		
		if(pthread_create(&th, NULL, passage, &connexion)!=0){
			printf("pthread_create\n");
			return EXIT_FAILURE;
		}
		
		
		i++;
		
		
	}
	
	shutdown(connexion, 2);
	close(sock);
	
	
	
	if( recvfrom(connexion, &nomFic, sizeof(nomFic), 0, NULL, NULL)==-1){
		perror("recvfrom");
		return EXIT_FAILURE;
		
	}
	
		
	while(1){
		if( recvfrom(connexion, &donnees, sizeof(donnees), 0, NULL, NULL)==-1){
			perror("recvfrom");
			return EXIT_FAILURE;
		}
		
		printf("%s\n", donnees);
		
		if( (nbEcrit=write(fd, donnees, strlen(donnees)))==-1){
			perror("write");
			return EXIT_FAILURE;
			
		}
		
		if(nbEcrit<N){
			printf("ICI %d\n", nbEcrit);
			
			break;
			
		}
		
	}

	
	return EXIT_SUCCESS;
}*/
