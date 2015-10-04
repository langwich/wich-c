#include <stdio.h>
#include "wich.h"

void bar(Vector * x);

void bar(Vector * x)
{
	COPY_ON_WRITE(x);
	x->data[1-1] = 100;
	print_vector(x);
}

int main(int argc, char *argv[])
{
    Vector *x;
	x = Vector_new((double []){1,2,3}, 3);
	bar(x);
	COPY_ON_WRITE(x);
	x->data[1-1] = 99;
	print_vector(x);
	return 0;
}
