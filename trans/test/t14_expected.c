#include <stdio.h>
#include "wich.h"

int x;

void f();

void f()
{
	g();
}

int main(int argc, char *argv[])
{
    x = 99;
	return 0;
}
