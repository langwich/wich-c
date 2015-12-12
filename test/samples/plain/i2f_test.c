#include <stdio.h>
#include "wich.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	PVector_ptr x;
	x = Vector_new((double []){1,2.0,3,4}, 4);
	while ((ith(x, (3)-1) > 0)) {
	    set_ith(x, 3-1, (ith(x, (3)-1) - 1));
	}
	print_vector(x);
	return 0;
}

