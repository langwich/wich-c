#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	STRING(x);
	x = String_new("Hello World!");
	REF((void *)x);
    EXIT();
	return 0;
}

