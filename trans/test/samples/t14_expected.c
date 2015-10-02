#include <stdio.h>
#include "wich.h"

int x;

void f();

void f()
{
	printf("%d\n", x);
}

int main(int argc, char *argv[])
{
	x = 99;
	return 0;
}
