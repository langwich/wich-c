#include <stdio.h>
#include "wich.h"
#include "gc.h"

void f(int x,PVector_ptr v);

void f(int x,PVector_ptr v)
{
	gc_begin_func();
	gc_end_func();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

