#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	VECTOR(u);
	VECTOR(v);
	u = Vector_new((double []){1,2,3}, 3);
	REF((void *)u.vector);
	v = Vector_new((double []){2,3,4}, 3);
	REF((void *)v.vector);
	printf("%1.2f\n", (ith(u, (1)-1) + ith(v, (3)-1)));
    EXIT();
	return 0;
}

