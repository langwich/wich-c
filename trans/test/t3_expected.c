#include <stdio.h>
#include "wich.h"

int main(int argc, char *argv[])
{
	String *x = String_new("Hello World!");
	DEREF(x);
	return 0;
}