#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>


typedef struct liste_mot{
	
	char mot[27];
	struct liste_mot * next;
	
}Liste_mot;

char tirer_lettre(char *mot);

char * generer_grille();

void detruire_grille(char *grille);

void afficher_grille(char *grille);

int score(char * mot_correct);

int verif_trajectoire(char *traj);

Liste_mot * charger_dico();

void free_dico(Liste_mot * l);

int mot_dans_dico(Liste_mot * dico, char * mot);
