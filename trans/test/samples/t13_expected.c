#include <stdio.h>
#include "wich.h"

String * f();
double g();

String * f()
{
	g();
}

double g()
{
	f();
}

int main(int argc, char *argv[])
{
	return 0;
}
