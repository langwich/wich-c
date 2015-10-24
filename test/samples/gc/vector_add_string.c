#include <stdio.h>
#include "wich.h"
#include "gc.h"

int main(int argc, char *argv[])
{
	setup_error_handlers();
	gc_begin_func();
	STRING(s);
	VECTOR(v);
	STRING(z);
	s = String_new("hello");
	v = Vector_new((double []){1,2,3}, 3);
	z = String_add(s,String_from_vector(v));
	print_string(z);
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

