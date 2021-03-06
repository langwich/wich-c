#include <stdio.h>
#include "wich.h"
#include "gc.h"

bool str_gt(String * s1,String * t);
void gt_msg(String * s,String * t);
void le_msg(String * s,String * t);

bool str_gt(String * s1,String * t)
{
	gc_begin_func();
	{gc_end_func(); return String_gt(s1,t);}

	gc_end_func();
}

void gt_msg(String * s,String * t)
{
	gc_begin_func();
	print_string(String_add(String_add(s,String_new(" is greater than ")),t));

	gc_end_func();
}

void le_msg(String * s,String * t)
{
	gc_begin_func();
	print_string(String_add(String_add(s,String_new(" is less than or equal to ")),t));

	gc_end_func();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	STRING(s1);
	STRING(s2);
	STRING(t);
	bool s1t;
	bool s2t;
	s1 = String_new("hellp");
	s2 = String_new("aello");
	t = String_new("hello");
	s1t = str_gt(s1,t);
	if (s1t) {
		gt_msg(s1,t);
	}
	else {
		le_msg(s1,t);
	}
	s2t = str_gt(s2,t);
	if (s2t) {
		gt_msg(s2,t);
	}
	else {
		le_msg(s2,t);
	}
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

