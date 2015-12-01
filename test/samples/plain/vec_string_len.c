#include <stdio.h>
#include "wich.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
	String * a;
	int b;
	a = String_new("hello");
	b = Vector_len(Vector_new((double []){1,2,3}, 3));
	printf("%d\n", ((String_len(a) + String_len(String_new("world"))) + b));
	return 0;
}

