#include <stdio.h>
#include "wich.h"
#include "gc.h"

int main(int argc, char *argv[])
{
	setup_error_handlers();
	gc_begin_func();
	VECTOR(x);
	VECTOR(y);
	VECTOR(z);
	VECTOR(q);
	x = Vector_new((double []){4,6,8}, 3);
	y = Vector_new((double []){2,3,4}, 3);
	z = Vector_mul(x,y);
	q = Vector_div(z,y);
	print_vector(q);
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

