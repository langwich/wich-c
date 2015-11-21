#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

bool bar(int x);

bool bar(int x)
{
    ENTER();
    {EXIT(); return (x < 10);}

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	int x;
	x = 5;
	printf("%d\n", bar(x));
    EXIT();
	return 0;
}

