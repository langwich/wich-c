#include <stdio.h>
#include "wich.h"
#include "gc.h"

int f(PVector_ptr a);

int f(PVector_ptr a)
{
	gc_begin_func();
	int x;
	STRING(b);
	VECTOR(e);
	x = 32;
	b = String_new("cat");
	{
		STRING(c);
		c = String_new("dog");
		{
			STRING(d);
			d = String_new("moo");
			{gc_end_func(); return x;}
		}
	}
	{
		STRING(b);
		b = String_new("boo");
	}
	e = Vector_new((double []){7}, 1);

	gc_end_func();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	printf("%d\n", f(Vector_new((double []){1}, 1)));
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

