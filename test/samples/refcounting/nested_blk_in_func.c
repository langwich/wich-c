#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

void f();

void f()
{
    ENTER();
    STRING(x);
    x = String_new("cat");
    REF((void *)x);
    {
    	MARK();
        STRING(y);
        STRING(z);
        y = String_new("dog");
        REF((void *)y);
        z = x;
        REF((void *)z);
        RELEASE();
    }

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	f();
    EXIT();
	return 0;
}

