#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

PVector_ptr f(double x);

PVector_ptr f(double x)
{
    ENTER();
    VECTOR(y);
    VECTOR(z);
    y = Vector_new((double []){1,2,3}, 3);
    REF((void *)y.vector);
    z = Vector_add(y,Vector_from_float(x,(y).vector->length));
    REF((void *)z.vector);
    {REF((void *)z.vector); EXIT(); DEC((void *)z.vector); return z;}

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	print_vector(f(4.00));
    EXIT();
	return 0;
}

