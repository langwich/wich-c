#include <stdio.h>
#include "wich.h"
#include "gc.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	int j;
	VECTOR(x);
	j = 1;
	x = Vector_new((double []){1,2}, 2);
	set_ith(x, j-1, ith(x, ((j + 1))-1));
	print_vector(x);
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

