#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int fib(int x);

int fib(int x)
{
    ENTER();
    if (((x == 0) || (x == 1))) {
    	MARK();
        {EXIT(); return x;}
        RELEASE();
    }
    {EXIT(); return (fib((x - 1)) + fib((x - 2)));}

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	printf("%d\n", fib(5));
    EXIT();
	return 0;
}

