#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	VECTOR(x);
	x = Vector_new((double []){1,2,3,4,5}, 5);
	REF((void *)x.vector);
    EXIT();
	return 0;
}

