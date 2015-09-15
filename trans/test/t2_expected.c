#include "wich.h"

int main(int argc, char *argv[]) {

	Vector *x = Vector_new((double []){1,2,3}, 3);

	DEREF(x);

	return 0;
}