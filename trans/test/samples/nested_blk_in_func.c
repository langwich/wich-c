#include <stdio.h>
#include "wich.h"

void f();

void f()
{
	String * x;
	x = String_new("cat");
    REF(x);
	{
		String * y;
		String * z;
		y = String_new("dog");
    	REF(y);
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
