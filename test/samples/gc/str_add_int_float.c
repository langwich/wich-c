#include <stdio.h>
#include "wich.h"
#include "gc.h"

int main(int argc, char *argv[])
{
	setup_error_handlers();
	gc_begin_func();
	STRING(s);
	int i;
	double f;
	STRING(r);
	s = String_new("hello");
	i = 1;
	f = 1.00;
	r = String_add(String_new("world"),String_from_float(f));
	print_string(String_add(s,String_from_int(i)));
	print_string(r);
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

