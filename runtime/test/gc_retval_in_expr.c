#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

func f() : [] {
	var x = [1, 2, 3]
	return x
}

print( f() + f() )
 */

Vector *f()
{
	// var x = [1, 2, 3]
	Vector *x = Vector_new((double []){1,2,3}, 3);

	// code for return statement is split in two parts; part 1 indicates a ref will escape this context
	REF(x); // count is now 2

	// end of scope code: drop ref count by 1 for all [], string vars
	DEREF(x);

	// code for return statement is split in two parts; part 2
	return x;
}

int main(int argc, char *argv[])
{
	// print( f() + f() )
	Vector *tmp1;
	Vector *tmp2;
	Vector *tmp3;
	print_vector( tmp3=Vector_add(tmp1=f(), tmp2=f()) );

	// end of global scope
	DEREF(tmp1);
	DEREF(tmp2);
	DEREF(tmp3);
}
