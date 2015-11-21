#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

void f(PVector_ptr a);

void f(PVector_ptr a)
{
    ENTER();
    STRING(b);
    VECTOR(e);
    REF((void *)a.vector);
    b = String_new("cat");
    REF((void *)b);
    {
    	MARK();
        STRING(c);
        c = String_new("dog");
        REF((void *)c);
        {
        	MARK();
            STRING(d);
            d = String_new("moo");
            REF((void *)d);
            RELEASE();
        }
        RELEASE();
    }
    {
    	MARK();
        STRING(b);
        STRING(c);
        b = String_new("boo");
        REF((void *)b);
        c = String_new("hoo");
        REF((void *)c);
        RELEASE();
    }
    e = Vector_new((double []){7}, 1);
    REF((void *)e.vector);

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
    EXIT();
	return 0;
}

