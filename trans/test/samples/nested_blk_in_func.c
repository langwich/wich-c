#include <stdio.h>
#include "wich.h"

void f();

void f()
{
	String * x;
	x = String_new("cat");
	{
		String * y;
		String * z;
		y = String_new("dog");
		z = x;
		REF(z);
		DEREF(y);
		DEREF(z);
	}
	DEREF(x);
}

int main(int argc, char *argv[])
{
	f();
	return 0;
}
