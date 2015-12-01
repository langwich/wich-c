#include <stdio.h>
#include "wich.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	PVector_ptr x;
	PVector_ptr y;
	x = Vector_new((double []){1,2,3}, 3);
	y = PVector_copy(x);
	set_ith(y, 1-1, 4);
	print_vector(x);
	return 0;
}

