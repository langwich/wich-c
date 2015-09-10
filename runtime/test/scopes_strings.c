#include <stdio.h>
#include "wich.h"

/* translation of Wich code:

func f() {
	var x = "cat"
    {
        var y = "dog"
        var z = x
    }
}

f()
 */

void f() {
	String *x = String_new("cat");
	{
		String *y = String_new("dot");

		String *z = x;
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
