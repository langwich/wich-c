#include <stdio.h>
#include "wich.h"

void f(int x,Vector * v);

void f(int x,Vector * v)
{
	REF(v);
	DEREF(v);
}


int main(int argc, char *argv[])
{
	return 0;
}
