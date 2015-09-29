#include <stdio.h>
#include "wich.h"

Vector * f();

Vector * f()
{
	Vector * x;
	x = Vector_new((double []){1,2,3}, 3);
	REF(x);
	DEREF(x);
	return x;
}

int main(int argc, char *argv[])
{
	Vector * tmp1;
	Vector * tmp2;
	Vector * tmp3;
	print_vector(tmp3=Vector_add(tmp1=f(),tmp2=f()));
	DEREF(tmp1);
	DEREF(tmp2);
	DEREF(tmp3);
	return 0;
}
