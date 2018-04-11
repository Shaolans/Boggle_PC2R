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

char ** generer_grille(){
	
	srand(time(NULL));
	
	char **grille;
	int presents[16]= {0};
	char c;
	int i,j,k;

	grille = malloc(sizeof(char*)*4);
	
	for(i=0; i<4; i++){
		grille[i] = malloc(sizeof(char)*4);
	}
	
	for(i=0; i<4; i++){
		for(j=0; j<4; j++){
				
				while( presents[(k=rand()%16)] );
			
				grille[i][j] = tirer_lettre(des[k]);
		}
		
	}
	
	return grille;
}

void detruire_grille(char **grille){
	
	int i;
	
	for(i=0; i<4; i++){
		free(grille[i]);
		
	}
	
	free(grille);
	
}



void afficher_grille(char **grille){
	int i,j;
	
	for(i=0; i<4; i++){
		for(j=0;j<4; j++){
			printf("%c\t", grille[i][j]);
		}
		printf("\n");
	}
			
}


int main(){
	
	char ** grille = generer_grille();
	
	afficher_grille(grille);
	
	detruire_grille(grille);
	
	
	return 0;
}
