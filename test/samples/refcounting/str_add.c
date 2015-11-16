#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	STRING(hello);
	STRING(world);
	hello = String_new("hello");
	REF((void *)hello);
	world = String_new("world");
	REF((void *)world);
	print_string(String_add(hello,world));
    EXIT();
	return 0;
}

