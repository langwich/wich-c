#include <stdio.h>
#include "wich.h"
int
main(int argc, char *argv[])
{
	setup_error_handlers();
    String *x;

    x = String_new("Hello World!");
    return 0;
}
