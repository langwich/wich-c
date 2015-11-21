#include <stdio.h>
#include "wich.h"
#include "gc.h"

String * f();
double g();

String * f()
{
	gc_begin_func();
	g();

	gc_end_func();
}

double g()
{
	gc_begin_func();
	f();

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

