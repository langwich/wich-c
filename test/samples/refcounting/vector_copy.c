#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	VECTOR(x);
	VECTOR(y);
	x = Vector_new((double []){1,2,3}, 3);
	REF((void *)x.vector);
	y = PVector_copy(x);
	REF((void *)y.vector);
	set_ith(y, 1-1, 4);
	print_vector(x);
    EXIT();
	return 0;
}

