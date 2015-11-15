#include <stdio.h>
#include "wich.h"

bool bar(int x);

bool bar(int x)
{
    return (x < 10);

}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	int x;
	x = 5;
	printf("%d\n", bar(x));
	return 0;
}

