#include <stdio.h>
#include "wich.h"

int x;

int main(int argc, char *argv[])
{
	x = 10;
	while (x > 0) {
		printf("%1.2f\n", x + 1.0);
		x = x - 1;
	}
	return 0;
}
