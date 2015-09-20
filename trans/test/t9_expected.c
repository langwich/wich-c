#include <stdio.h>
#include "wich.h"

String *becomeSuper(String *name) {
	REF(name);

	String *tmp1;
	String *tmp2 = String_add(tmp1=String_new("super"), name);

	REF(tmp2);

	DEREF(name);
	DEREF(tmp1);
	DEREF(tmp2);

	return tmp2;
}

int main(int argc, char *argv[]) {

	String *tmp3;
	String *tmp4;
	print_string(tmp4=becomeSuper(tmp3=String_new("man")));

	String *tmp5;
	String *tmp6;
	print_string(tmp6=becomeSuper(tmp5=String_new("duper")));

	DEREF(tmp3);
	DEREF(tmp4);
	DEREF(tmp5);
	DEREF(tmp6);

	return 0;
}

