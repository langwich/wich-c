#include <stdio.h>
#include "wich.h"

String * becomeSuper(String * name);

String * becomeSuper(String * name)
{
	String *_retv;
	String *tmp1;
	REF(name);
	tmp1=String_add(String_new("super"),name);
	REF(tmp1);
	_retv = tmp1;
_cleanup:
	DEREF(name);
	DEREF(tmp1);
	return _retv;
}

int main(int argc, char *argv[])
{
	print_string(becomeSuper(String_new("man")));
	print_string(becomeSuper(String_new("duper")));
	return 0;
}
