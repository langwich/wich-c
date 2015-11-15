#include <stdio.h>
#include "wich.h"
#include "gc.h"

bool foo(int x);
bool bar(int x);

bool foo(int x)
{
	gc_begin_func();
	{gc_end_func(); return (x < 10);}

	gc_end_func();
}

bool bar(int x)
{
	gc_begin_func();
	if ((x < 1)) {
		{gc_end_func(); return true;}
	}
	else {
		{gc_end_func(); return false;}
	}

	gc_end_func();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	bool x;
	bool y;
	x = bar(5);
	y = foo(1);
	printf("%d\n", (x || y));
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

