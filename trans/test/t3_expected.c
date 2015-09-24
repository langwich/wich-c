#include <stdio.h>
#include "wich.h"

String * x;

int main(int argc, char *argv[])
{
	x = String_new("Hello World!");
	DEREF(x);
	return 0;
}
