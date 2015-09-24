#include <stdio.h>
#include "wich.h"

String * f();
float g();

String * f()
{
	g();
}
float g()
{
	f();
}

int main(int argc, char *argv[])
{
	return 0;
}
