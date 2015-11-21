#include <stdio.h>
#include "wich.h"
#include "gc.h"

void f();

void f()
{
	gc_begin_func();
	STRING(x);
	x = String_new("cat");
	{
		STRING(y);
		STRING(z);
		y = String_new("dog");
		z = x;
	}

	gc_end_func();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	f();
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

