#include <stdio.h>
#include "wich.h"

bool foo(int x);
bool bar(int x);

bool foo(int x)
{
    return (x < 10);

}

bool bar(int x)
{
    if ((x < 1)) {
        return true;
    }
    else {
        return false;
    }

}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	bool x;
	bool y;
	x = bar(5);
	y = foo(1);
	printf("%d\n", (x || y));
	return 0;
}

