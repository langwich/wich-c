#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

PVector_ptr foo();

PVector_ptr foo()
{
    ENTER();
    {EXIT(); return Vector_new((double []){1,2,3,4,5}, 5);}

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();

    VECTOR(x);
    x = PVector_copy(foo());
    REF((void *)x.vector);
    print_vector(foo());
    EXIT();
	return 0;
}

