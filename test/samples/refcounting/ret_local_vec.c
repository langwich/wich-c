#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

PVector_ptr f();

PVector_ptr f()
{
    ENTER();
    VECTOR(x);
    x = Vector_new((double []){1,2,3}, 3);
    REF((void *)x.vector);
    {REF((void *)x.vector); EXIT(); DEC((void *)x.vector); return x;}

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	print_vector(Vector_add(f(),f()));
    EXIT();
	return 0;
}

