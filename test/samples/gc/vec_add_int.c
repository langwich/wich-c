#include <stdio.h>
#include "wich.h"
#include "gc.h"

PVector_ptr f(int x);

PVector_ptr f(int x)
{
	gc_begin_func();
	VECTOR(y);
	VECTOR(z);
	y = Vector_new((double []){1,2,3}, 3);
	z = Vector_add(y,Vector_from_int(x,(y).vector->length));
	{gc_end_func(); return z;}

	gc_end_func();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	print_vector(f(4));
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

