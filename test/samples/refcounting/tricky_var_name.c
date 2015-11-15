#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	int argc;
	STRING(argv);
	argc = 1;
	argv = String_new("hello world");
	REF((void *)argv);
	print_string(String_add(argv,String_from_int(argc)));
    EXIT();
	return 0;
}

