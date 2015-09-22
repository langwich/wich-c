#include <stdio.h>
#include "wich.h"

int main(int argc, char *argv[])
{

	Vector *x = Vector_new((double []){1,2,3,4,5}, 5);
	DEREF(x);
	return 0;
}