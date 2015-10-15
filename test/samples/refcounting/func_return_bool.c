#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
bool bar(int x);

bool bar(int x){
	ENTER();
	{
		EXIT();
		return (x<10);
	}
	EXIT();
}

int
main(int argc, char *argv[])
{
	setup_error_handlers();
	ENTER();
	int x;
	x =5;
	printf("%d\n", bar(x));
	EXIT();
	return 0;
}