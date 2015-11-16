#include <stdio.h>
#include "wich.h"

String * becomeSuper(String * name);

String * becomeSuper(String * name)
{
    return String_add(String_new("super"),name);

}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	print_string(becomeSuper(String_new("man")));
	print_string(becomeSuper(String_new("duper")));
	return 0;
}

