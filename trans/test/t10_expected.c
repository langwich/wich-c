#include <stdio.h>
#include "wich.h"

void f()
{
	String *x = String_new("cat");
	{
		String *y = String_new("dog");

		String *z = x;
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