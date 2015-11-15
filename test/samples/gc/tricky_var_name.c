#include <stdio.h>
#include "wich.h"
#include "gc.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	int argc;
	STRING(argv);
	argc = 1;
	argv = String_new("hello world");
	print_string(String_add(argv,String_from_int(argc)));
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

