#include <stdio.h>
#include "wich.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	int argc;
	String * argv;
	argc = 1;
	argv = String_new("hello world");
	print_string(String_add(argv,String_from_int(argc)));
	return 0;
}

