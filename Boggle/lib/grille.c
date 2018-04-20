#include "grille.h"


char des[16][6] = { "ETUKNO",
				"EVGTIN",
				"DECAMP",
				"IELRUW",
				"EHIFSE",
	"RECALS",
	"ENTDOS",
	"OFXRIA",
	"NAVEDZ",
	"EIOATA",
	"GLENYU",
	"BMAQJO",
	"TLIBRA",
	"SPULTE",
	"AIMSOR",
	"ENHRIS"};
	
	
char tirer_lettre(char *mot){
	
	int i = rand()%6;
	
	return mot[i];
	
}

char * generer_grille(){
	
	
	
	char *grille;
	int presents[16]= {0};
	int i,j,k;
	
	srand(time(NULL));

	grille = malloc(sizeof(char*)*16);
	
	for(i=0; i<4; i++){
		for(j=0; j<4; j++){
				
				while( presents[(k=rand()%16)] );
			
				grille[i*4+j] = tirer_lettre(des[k]);
		}
		
	}
	
	return grille;
}

void detruire_grille(char *grille){
	
	free(grille);
	
}



void afficher_grille(char *grille){
	int i,j;
	
	for(i=0; i<4; i++){
		for(j=0;j<4; j++){
			printf("%c\t", grille[i*4+j]);
		}
		printf("\n");
	}
			
}

int score(char * mot_correct){
	
	int taille = (int)strlen(mot_correct);
	
	if(taille < 3)
		return 0;
		
	if(taille < 5)
		return 1;
	
	if(taille < 6)
		return 2;
		
	if(taille < 7)
		return 3;
	
	if(taille < 8)
		return 5;
	
	return 11;
	
}


int verif_trajectoire(char * traj){
	
	int taille = strlen(traj);
	int k=0;
	char cell[2];
	char cell2compare[2];
	int trajCorrect = 1;
	
	while(k<taille-2){
		
		cell[0] = traj[k++];
		cell[1] = traj[k++];
		
		cell2compare[0] = traj[k];
		cell2compare[1] = traj[k+1];
		
		if(strcmp(cell, "A1")==0){
			if(strcmp(cell2compare, "A2") && strcmp(cell2compare, "B1") && strcmp(cell2compare, "B2") ){
				trajCorrect=0;
				break;
			}
			
		}
		else{
			if(strcmp(cell, "B1")==0){
				if(strcmp(cell2compare, "A1") && strcmp(cell2compare, "A2") && strcmp(cell2compare, "B2") && strcmp(cell2compare, "C1") && strcmp(cell2compare, "C2") ){
					trajCorrect=0;
					break;
				}
				
			}
			else{
				if(strcmp(cell, "C1")==0){
					if(strcmp(cell2compare, "B1") && strcmp(cell2compare, "B2") && strcmp(cell2compare, "C2") && strcmp(cell2compare, "D1") && strcmp(cell2compare, "D2") ){
						trajCorrect=0;
						break;
					}
				
				}
				else{
					
					if(strcmp(cell, "D1")==0){
						if(strcmp(cell2compare, "C1") && strcmp(cell2compare, "C2") && strcmp(cell2compare, "D2") ){
							trajCorrect=0;
							break;
						}
				
					}
					else{
						
						if(strcmp(cell, "A2")==0){
							if(strcmp(cell2compare, "A1") && strcmp(cell2compare, "A3") && strcmp(cell2compare, "B1") && strcmp(cell2compare, "B2") && strcmp(cell2compare, "B3") ){
								trajCorrect=0;
								break;
							}
					
						}
						else{
							
							if(strcmp(cell, "B2")==0){
								if(strcmp(cell2compare, "A1") && strcmp(cell2compare, "A2") && strcmp(cell2compare, "A3") && strcmp(cell2compare, "B1") && strcmp(cell2compare, "B3") && strcmp(cell2compare, "C1") && strcmp(cell2compare, "C2") && strcmp(cell2compare, "C3") ){
									trajCorrect=0;
									break;
								}
						
							}
							else{
								if(strcmp(cell, "C2")==0){
									if(strcmp(cell2compare, "B1") && strcmp(cell2compare, "B2") && strcmp(cell2compare, "B3") && strcmp(cell2compare, "C1") && strcmp(cell2compare, "C3") && strcmp(cell2compare, "D1") && strcmp(cell2compare, "D2") && strcmp(cell2compare, "D3") ){
										trajCorrect=0;
										break;
									}
							
								}
								else{
									
									if(strcmp(cell, "D2")==0){
										if(strcmp(cell2compare, "C1") && strcmp(cell2compare, "C2") && strcmp(cell2compare, "C3") && strcmp(cell2compare, "D1") && strcmp(cell2compare, "D3") ){
											trajCorrect=0;
											break;
										}
						
									}
									else{
										
										if(strcmp(cell, "A3")==0){
											if(strcmp(cell2compare, "A2") && strcmp(cell2compare, "A4") && strcmp(cell2compare, "B2") && strcmp(cell2compare, "B3") && strcmp(cell2compare, "B4") ){
												trajCorrect=0;
												break;
											}
									
										}
										else{
											
											if(strcmp(cell, "B3")==0){
												if(strcmp(cell2compare, "A2") && strcmp(cell2compare, "A3") && strcmp(cell2compare, "A4") && strcmp(cell2compare, "B2") && strcmp(cell2compare, "B4") && strcmp(cell2compare, "C2") && strcmp(cell2compare, "C3") && strcmp(cell2compare, "C4") ){
													trajCorrect=0;
													break;
												}
										
											}
											else{
												if(strcmp(cell, "C3")==0){
													if(strcmp(cell2compare, "B2") && strcmp(cell2compare, "B3") && strcmp(cell2compare, "B4") && strcmp(cell2compare, "C2") && strcmp(cell2compare, "C4") && strcmp(cell2compare, "D2") && strcmp(cell2compare, "D3") && strcmp(cell2compare, "D4") ){
														trajCorrect=0;
														break;
													}
											
												}
												else{
													
													if(strcmp(cell, "D3")==0){
														if(strcmp(cell2compare, "C1") && strcmp(cell2compare, "C2") && strcmp(cell2compare, "C3") && strcmp(cell2compare, "D1") && strcmp(cell2compare, "D3") ){
															trajCorrect=0;
															break;
														}
										
													}
													else{
														if(strcmp(cell, "A4")==0){
															if(strcmp(cell2compare, "A3") && strcmp(cell2compare, "B3") && strcmp(cell2compare, "B4") ){
																trajCorrect=0;
																break;
															}
					
														}
														else{
															
															if(strcmp(cell, "B4")==0){
																if(strcmp(cell2compare, "A3") && strcmp(cell2compare, "A4") && strcmp(cell2compare, "B3") && strcmp(cell2compare, "C3") && strcmp(cell2compare, "C4") ){
																	trajCorrect=0;
																	break;
																}
														
															}
															else{
																if(strcmp(cell, "C4")==0){
																	if(strcmp(cell2compare, "B3") && strcmp(cell2compare, "B4") && strcmp(cell2compare, "C3") && strcmp(cell2compare, "D3") && strcmp(cell2compare, "D4") ){
																		trajCorrect=0;
																		break;
																	}
															
																}
																else{
																	
																	if(strcmp(cell, "D4")==0){
																		if(strcmp(cell2compare, "C3") && strcmp(cell2compare, "C4") && strcmp(cell2compare, "D3") ){
																			trajCorrect=0;
																			break;
																		}
														
																	}
																	else{
																	
																		trajCorrect = 0;
																		break;
																		
																	
																	}	
																	
																	
																	
																
																}	
																
																
																
																
															
															}							
															
															
														
														}						
														
														
														
														
													
													}	
													
													
													
												
												}	
												
												
												
												
											
											}							
											
											
										
										}						
										
										
										
									
									}	
									
									
									
								
								}	
								
								
								
								
							
							}							
							
							
						
						}						
					
					}
						
					
				}
					
				
				
			}
			
		}
		
		
	}
	
	
	return trajCorrect;
	
}

Liste_mot * charger_dico(){
	
	FILE* fichier = NULL;

    char mot[27] = "";
	Liste_mot * l = NULL, *lp = NULL, *res = NULL ;
 

    fichier = fopen("./liste_francais.txt", "r");

	l = lp;

    if (fichier != NULL){

        while(fgets(mot, 27, fichier)){ 
			
			if(res==NULL){
				l= malloc(sizeof(struct liste_mot));
				memcpy(l->mot, mot, strlen(mot)-2);
				l->mot[strlen(mot)-2] = '\0';
				res = l;
			}
			
			
			lp = malloc(sizeof(struct liste_mot));
			
			memcpy(lp->mot, mot, strlen(mot)-2);
			lp->mot[strlen(mot)-2] = '\0';
			
			l->next = lp;
			l = lp;

		}

        fclose(fichier);

    }
	
	return res;
	
}

void free_dico(Liste_mot * l){
	
	Liste_mot *lp;
	
	while(l){
		lp = l;
		l = l->next;
		free(lp);
	}
	
	
}


int mot_dans_dico(Liste_mot *dico, char *mot){
	
	Liste_mot *lp;
	
	lp = dico;
	
	while(lp){
		
		if(strcmp(lp->mot, mot)==0){
			return 1;
		}
		lp = lp->next;
		
	}
	
	return 0;
	
}

