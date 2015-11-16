#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	int x;
	int y;
	x = 2;
	y = 1;
	if ((x > y)) {
		MARK();
	    print_string(String_new("TRUE"));
	    RELEASE();
	}
	else {
		MARK();
	    print_string(String_new("FALSE"));
	    RELEASE();
	}
    EXIT();
	return 0;
}

