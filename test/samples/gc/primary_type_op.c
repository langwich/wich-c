#include <stdio.h>
#include "wich.h"
#include "gc.h"

double f(int x);

double f(int x)
{
	gc_begin_func();
	double y;
	y = 1.0;
	{gc_end_func(); return (x + -y);}

	gc_end_func();
}


int main(int argc, char *argv[])
{
	setup_error_handlers();
	gc_begin_func();
	double z;
	z = f(2);
	if ((z == 0)) {
		print_string(String_new("z==0"));
	}
	else {
		print_string(String_new("z!=0"));
	}
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

