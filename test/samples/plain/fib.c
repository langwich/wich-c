#include <stdio.h>
#include "wich.h"

int fib(int x);

int fib(int x)
{
    if (((x == 0) || (x == 1))) {
        return x;
    }
    return (fib((x - 1)) + fib((x - 2)));

}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	printf("%d\n", fib(5));
	return 0;
}

