#include <stdio.h>
#include "wich.h"

Vector * x;

Vector * foo();

Vector * foo()
{
	Vector * y;
	y = Vector_new((double []){1,2,3,4,5}, 5);
	REF(y);
	DEREF(y);
	return y;
    DEREF(y);
}

int main(int argc, char *argv[])
{
	x = foo();
    REF(x);
	print_vector(foo());
	DEREF(x);
	return 0;
}
