#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	STRING(s);
	int i;
	double f;
	STRING(r);
	s = String_new("hello");
	REF((void *)s);
	i = 1;
	f = 1.00;
	r = String_add(String_new("world"),String_from_float(f));
	REF((void *)r);
	print_string(String_add(s,String_from_int(i)));
	print_string(r);
    EXIT();
	return 0;
}

