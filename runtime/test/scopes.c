#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

func f() {
	var x = [1, 2, 3]
    {
        var y = [9,10]
        var z = x
    }
}

f()
 */

void f() {
	// var x = [1, 2, 3]
	Vector *x = Vector_new((double[]) {1, 2, 3}, 3);

	{
		Vector *y = Vector_new((double[]) {9, 10}, 2);

		Vector *z = x;
		REF(z);

		// end of scope; deref locals
		DEREF(y);
		DEREF(z);
	}

	// end of scope code; drop ref count by 1 for all [], string vars
	DEREF(x);
}

int main(int argc, char *argv[])
{
	f();
}
