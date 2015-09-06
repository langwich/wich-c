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
	((heap_object *)x)->refs = 1; // maybe better to set specifically to 1 as part of code gen not refs++

	// x[1] = 99
	if ( x!=NULL && ((heap_object *)x)->refs > 1 ) {
		((heap_object *)x)->refs--; // no longer referring to old value (if any)
		x = Vector_copy(x);
		((heap_object *)x)->refs = 1;
	}
	x->data[1-1] = 99;

	// var y = x
	Vector *y = x;
	((heap_object *)y)->refs++;

	// y[2] = 101
	COPY_ON_WRITE(y); // I made into a macro, see wich.h
	y->data[2-1] = 101;

	// print(x)
	printf("%s\n", Vector_as_string(x));
	// print(y)
	printf("%s\n", Vector_as_string(y));

	// end of global scope
	((heap_object *)x)->refs--;
	((heap_object *)y)->refs--;
	// for ref counting GC, here is where we'd check for ref count == 0 to free()
}
