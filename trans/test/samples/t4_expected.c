#include <stdio.h>
#include "wich.h"

int x;
int y;

int main(int argc, char *argv[])
{
	x = 2;
	y = 1;
	if (x > y) {
		String * tmp1;
		print_string(tmp1=String_new("TRUE"));
		DEREF(tmp1);
	}
	else {
		String * tmp2;
		print_string(tmp2=String_new("FALSE"));
		DEREF(tmp2);
	}
	return 0;
}
