#include <stdio.h>
#include "wich.h"

bool f(String * s);

bool f(String * s)
{
    if (String_le(s,String_new("cat"))) {
        return true;
    }
    return false;

}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	String * s1;
	String * s2;
	s1 = String_new("");
	s2 = String_new("cat");
	if (String_gt(s1,s2)) {
	}
	else {
	    print_string(String_new("miaow"));
	}
	printf("%d\n", f(s2));
	return 0;
}

