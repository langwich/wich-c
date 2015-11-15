#include <stdio.h>
#include "wich.h"
#include "gc.h"

void bar(PVector_ptr x);

void bar(PVector_ptr x)
{
	gc_begin_func();
	set_ith(x, 1-1, 100);
	print_vector(x);

	gc_end_func();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	VECTOR(x);
	x = Vector_new((double []){1,2,3}, 3);
	bar(x);
	set_ith(x, 1-1, 99);
	print_vector(x);
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

