#include <stdio.h>
#include "wich.h"
#include "gc.h"

PVector_ptr foo(int x);

PVector_ptr foo(int x)
{
	gc_begin_func();
	VECTOR(y);
	VECTOR(z);
	y = Vector_new((double []){2,4,6}, 3);
	z = Vector_div(y,Vector_from_int(x,(y).vector->length));
	{gc_end_func(); return z;}

	gc_end_func();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	double f;
	VECTOR(v);
	f = 5.00;
	v = Vector_mul(foo(2),Vector_from_float(f,(foo(2)).vector->length));
	print_vector(v);
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

