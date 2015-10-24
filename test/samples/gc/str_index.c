#include <stdio.h>
#include "wich.h"
#include "gc.h"

void f();

void f()
{
	gc_begin_func();
	STRING(x);
	x = String_add(String_new("cat"),String_new("dog"));
	print_string(x);
	print_string(String_add(String_from_char(x->str[(1)-1]),String_from_char(x->str[(3)-1])));

	gc_end_func();
}


int main(int argc, char *argv[])
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

