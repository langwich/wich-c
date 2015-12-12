#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	VECTOR(x);
	x = Vector_new((double []){1,2.0,3,4}, 4);
	REF((void *)x.vector);
	while ((ith(x, (3)-1) > 0)) {
		MARK();
	    set_ith(x, 3-1, (ith(x, (3)-1) - 1));
	    RELEASE();
	}
	print_vector(x);
    EXIT();
	return 0;
}

