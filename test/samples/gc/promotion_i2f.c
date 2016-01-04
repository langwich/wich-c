#include <stdio.h>
#include "wich.h"
#include "gc.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	int x;
	double y;
	x = 1;
	y = (3.14 + x);
	printf("%1.2f\n", y);
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

