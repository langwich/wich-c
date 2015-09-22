#include <stdio.h>
#include "wich.h"

int main(int argc, char *argv[])
{

	int x = 2;

	int y = 1;

	if (x > y) {
		String *tmp1;
		print_string(tmp1 = String_new("TRUE"));
		DEREF(tmp1);
	}
	else {
		String *tmp2;
		print_string(tmp2 = String_new("FALSE"));
		DEREF(tmp2);
	}
	return 0;
}