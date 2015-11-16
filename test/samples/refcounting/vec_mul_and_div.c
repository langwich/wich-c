#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

PVector_ptr foo(int x);

PVector_ptr foo(int x)
{
    ENTER();
    VECTOR(y);
    VECTOR(z);
    y = Vector_new((double []){2,4,6}, 3);
    REF((void *)y.vector);
    z = Vector_div(y,Vector_from_int(x,(y).vector->length));
    REF((void *)z.vector);
    {REF((void *)z.vector); EXIT(); DEC((void *)z.vector); return z;}

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	double f;
	VECTOR(v);
	f = 5.00;
	v = Vector_mul(foo(2),Vector_from_float(f,(foo(2)).vector->length));
	REF((void *)v.vector);
	print_vector(v);
    EXIT();
	return 0;
}

