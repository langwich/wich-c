#include <stdio.h>
#include "wich.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	PVector_ptr v;
	PVector_ptr w;
	v = Vector_new((double []){1.0,2.0,3.0}, 3);
	v = Vector_add(v,Vector_from_int(4,(v).vector->length));
	w = Vector_add(Vector_from_int(100,(v).vector->length),v);
	print_vector(v);
	print_vector(w);
	return 0;
}

