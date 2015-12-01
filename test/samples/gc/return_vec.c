#include <stdio.h>
#include "wich.h"
#include "gc.h"

PVector_ptr foo();

PVector_ptr foo()
{
	gc_begin_func();
	{gc_end_func(); return Vector_new((double []){1,2,3,4,5}, 5);}

	gc_end_func();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	VECTOR(x);
	x = PVector_copy(foo());
	print_vector(foo());
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

