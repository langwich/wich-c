#include <stdio.h>
#include "wich.h"

String *becomeSuper(String *name)
{
	REF(name);
	String *tmp5;
	String *tmp6 = String_add(tmp1=String_new("super"), name);
	REF(tmp6);
	DEREF(name);
	DEREF(tmp5);
	DEREF(tmp6);
	return tmp6;
}
int main(int argc, char *argv[])
{
	String *tmp1;
	String *tmp2;
	print_string(tmp2=becomeSuper(tmp1=String_new("man")));

	String *tmp3;
	String *tmp4;
	print_string(tmp4=becomeSuper(tmp3=String_new("duper")));

	DEREF(tmp1);
	DEREF(tmp2);
	DEREF(tmp3);
	DEREF(tmp4);
	return 0;
}

