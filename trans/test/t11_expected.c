#include <stdio.h>
#include "wich.h"

void f()
{
	String *tmp1;
	String *tmp2;
	String *x = String_add(tmp1=String_new("cat"), tmp2=String_new("dog"));

	print_string(x);

	String *tmp3;
	String *tmp4;
	String *tmp5;
	print_string(tmp5=String_add(tmp3=String_from_char(x->str[(1)-1]),tmp4=String_from_char(x->str[(3)-1])));

	DEREF(x);
	DEREF(tmp1);
	DEREF(tmp2);
	DEREF(tmp3);
	DEREF(tmp4);
	DEREF(tmp5);
}

int main(int argc, char *argv[])
{
	f();
	return 0;
}