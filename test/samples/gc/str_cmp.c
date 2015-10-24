#include <stdio.h>
#include "wich.h"
#include "gc.h"

bool f(String * s);

bool f(String * s)
{
	gc_begin_func();
	if ((s <= String_new("cat"))) {
		{gc_end_func(); return true;}
	}
	{gc_end_func(); return false;}

	gc_end_func();
}


int main(int argc, char *argv[])
{
	setup_error_handlers();
	gc_begin_func();
	STRING(s1);
	STRING(s2);
	s1 = String_new("");
	s2 = String_new("cat");
	if ((s1 > s2)) {
	}
	else {
		print_string(String_new("miaow"));
	}
	printf("%d\n", f(s2));
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

