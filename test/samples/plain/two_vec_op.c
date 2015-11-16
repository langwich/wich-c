#include <stdio.h>
#include "wich.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	PVector_ptr x;
	PVector_ptr y;
	PVector_ptr z;
	PVector_ptr q;
	x = Vector_new((double []){4,6,8}, 3);
	y = Vector_new((double []){2,3,4}, 3);
	z = Vector_mul(x,y);
	q = Vector_div(z,y);
	print_vector(q);
	return 0;
}

