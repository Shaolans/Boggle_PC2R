#			Programmation Repartie (4I400)
#		Makefile pour TD 9. Socket et parallélisme


# Documentation: http://www.gnu.org/software/make/manual/make.html
CC =gcc
LDFLAGS = -lpthread
CFLAGS =-W -Wall -std=c11 -ansi -pedantic -Iinclude

DIR=.
BIN=$(DIR)/bin/
OBJ=$(DIR)/obj/
INCLUDE=$(DIR)/include/
LIB=$(DIR)/lib/
SRC=$(DIR)/src/

HC=include/grille.h include/hashmap.h


.SUFFIXES:
.PHONY: all clean test test-serveur
all:  $(BIN)serveur $(BIN)client

test: test-serveur

#test-ftp_multi_serveur  $(BIN)ftp_multi_serveur

#test-ftp_multi_serveur: $(BIN)ftp_multi_serveur
#	-$$PWD/bin/ftp_multi_serveur 2000 /tmp &

test-serveur: $(BIN)serveur
	-$$PWD/bin/serveur -port 2006 -tours 6 -grilles ASZDEFRGHTYJKUIL CDVFBGNHUYIKOPLK
	
test-client: $(BIN)client
	-$$PWD/bin/client 127.0.0.1 2007
	
$(BIN)serveur: $(OBJ)serveur.o $(OBJ)grille.o $(OBJ)hashmap.o
	@if [ -d $(BIN) ]; then : ; else mkdir $(BIN); fi
	$(CC) -o $@ $^ $(LDFLAGS)


$(BIN)%: $(OBJ)%.o
	@if [ -d $(BIN) ]; then : ; else mkdir $(BIN); fi
	$(CC) -o $@ $^ $(LDFLAGS)

$(OBJ)%.o: $(SRC)%.c $(HC)
	@if [ -d $(OBJ) ]; then : ; else mkdir $(OBJ); fi
	$(CC) $(CFLAGS) -o $@ -c $<

$(OBJ)%.o: $(LIB)%.c $(HC)
	@if [ -d $(OBJ) ]; then : ; else mkdir $(OBJ); fi
	$(CC) $(CFLAGS) -o $@ -c $<

$(INCLUDE)%.h:
	@if [ -d $(INCLUDE) ]; then : ; else mkdir $(INCLUDE); fi

clean: 
	rm $(OBJ)*.o $(BIN)*
