#include <stdio.h>
#include "wich.h"

Vector * x;

int main(int argc, char *argv[])
{
	x = Vector_new((double []){1,2,3,4,5}, 5);
	REF(x);
	DEREF(x);
	return 0;
}
