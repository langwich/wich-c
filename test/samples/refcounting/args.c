#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

void f(int x,PVector_ptr v);

void f(int x,PVector_ptr v)
{
    ENTER();
    REF((void *)v.vector);

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
    EXIT();
	return 0;
}

