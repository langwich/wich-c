#include <stdio.h>
#include "wich.h"
#include "gc.h"

void f(PVector_ptr a);

void f(PVector_ptr a)
{
	gc_begin_func();
	STRING(b);
	VECTOR(e);
	b = String_new("cat");
	{
		STRING(c);
		c = String_new("dog");
		{
			STRING(d);
			d = String_new("moo");
		}
	}
	{
		STRING(b);
		STRING(c);
		b = String_new("boo");
		c = String_new("hoo");
	}
	e = Vector_new((double []){7}, 1);

	gc_end_func();
}


int main(int argc, char *argv[])
{
	setup_error_handlers();
	gc_begin_func();
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

