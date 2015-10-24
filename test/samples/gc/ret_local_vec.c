#include <stdio.h>
#include "wich.h"
#include "gc.h"

PVector_ptr f();

PVector_ptr f()
{
	gc_begin_func();
	VECTOR(x);
	x = Vector_new((double []){1,2,3}, 3);
	{gc_end_func(); return x;}

	gc_end_func();
}


int main(int argc, char *argv[])
{
	setup_error_handlers();
	gc_begin_func();
	print_vector(Vector_add(f(),f()));
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

