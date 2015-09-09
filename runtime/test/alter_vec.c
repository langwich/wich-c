#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

var x = [1, 2, 3]
x[1] = 99
var y = x
y[2] = 101 // copy before writing to y

print(x)   // [99, 2, 3]
print(y)   // [99, 101, 3]

 */

int main(int argc, char *argv[])
{
	// var x = [1, 2, 3]
	Vector *x = Vector_new((double []){1,2,3}, 3);

	// x[1] = 99
	COPY_ON_WRITE(x);
	x->data[1-1] = 99;

	// var y = x
	Vector *y = x;
	REF(y);

	// y[2] = 101
	COPY_ON_WRITE(y); // I made into a macro, see wich.h
	y->data[2-1] = 101;

	// print(x)
	print_vector(x);
	// print(y)
	print_vector(y);

	// end of global scope
	DEREF(x);
	DEREF(y);
}
