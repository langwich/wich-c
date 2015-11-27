#include <stdio.h>
#include "wich.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	int j;
	PVector_ptr x;
	j = 1;
	x = Vector_new((double []){1,2}, 2);
	set_ith(x, j-1, ith(x, ((j + 1))-1));
	print_vector(x);
	return 0;
}

