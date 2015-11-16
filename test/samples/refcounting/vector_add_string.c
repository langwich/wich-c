#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	STRING(s);
	VECTOR(v);
	STRING(z);
	s = String_new("hello");
	REF((void *)s);
	v = Vector_new((double []){1,2,3}, 3);
	REF((void *)v.vector);
	z = String_add(s,String_from_vector(v));
	REF((void *)z);
	print_string(z);
    EXIT();
	return 0;
}

