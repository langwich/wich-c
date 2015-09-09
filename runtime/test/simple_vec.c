#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

var x = [1, 2, 3]
var y = x
print(y)

 */

int main(int argc, char *argv[])
{
	// var x = [1, 2, 3]
	Vector *x = Vector_new((double []){1,2,3}, 3);

	// var y = x
	Vector *y = x;
	REF(y);

	// print(y)
	print_vector(y);

	// end of global scope
	DEREF(x);
	DEREF(y);
}
