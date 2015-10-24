#include <stdio.h>
#include "wich.h"
#include "gc.h"

String * becomeSuper(String * name);

String * becomeSuper(String * name)
{
	gc_begin_func();
	{gc_end_func(); return String_add(String_new("super"),name);}

	gc_end_func();
}


int main(int argc, char *argv[])
{
	setup_error_handlers();
	gc_begin_func();
	print_string(becomeSuper(String_new("man")));
	print_string(becomeSuper(String_new("duper")));
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

