#include <stdio.h>
#include "wich.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	int x;
	int y;
	x = 2;
	y = 1;
	if ((x > y)) {
	    print_string(String_new("TRUE"));
	}
	else {
	    print_string(String_new("FALSE"));
	}
	return 0;
}

