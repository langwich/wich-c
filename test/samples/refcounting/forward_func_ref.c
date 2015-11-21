#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

String * f();
double g();

String * f()
{
    ENTER();
    g();

    EXIT();
}

double g()
{
    ENTER();
    f();

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
    EXIT();
	return 0;
}

