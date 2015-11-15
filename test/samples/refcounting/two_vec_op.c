#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	VECTOR(x);
	VECTOR(y);
	VECTOR(z);
	VECTOR(q);
	x = Vector_new((double []){4,6,8}, 3);
	REF((void *)x.vector);
	y = Vector_new((double []){2,3,4}, 3);
	REF((void *)y.vector);
	z = Vector_mul(x,y);
	REF((void *)z.vector);
	q = Vector_div(z,y);
	REF((void *)q.vector);
	print_vector(q);
    EXIT();
	return 0;
}

