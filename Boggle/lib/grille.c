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
	
	srand(time(NULL));
	
	char *grille;
	int presents[16]= {0};
	char c;
	int i,j,k;

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

