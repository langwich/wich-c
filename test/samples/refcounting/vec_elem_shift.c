#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	int j;
	VECTOR(x);
	j = 1;
	x = Vector_new((double []){1,2}, 2);
	REF((void *)x.vector);
	set_ith(x, j-1, ith(x, ((j + 1))-1));
	print_vector(x);
    EXIT();
	return 0;
}

