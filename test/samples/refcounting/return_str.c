#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

String * becomeSuper(String * name);

String * becomeSuper(String * name)
{
    ENTER();
    REF((void *)name);
    {EXIT(); return String_add(String_new("super"),name);}

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	print_string(becomeSuper(String_new("man")));
	print_string(becomeSuper(String_new("duper")));
    EXIT();
	return 0;
}

