#include <stdio.h>
#include "wich.h"
#include "refcounting.h"
PVector_ptr bubbleSort(PVector_ptr vec);

PVector_ptr
bubbleSort(PVector_ptr vec)
{
	ENTER();
	int length;

	VECTOR(v);
	int i;

	int j;

	REF((void *)vec.vector);
	length = Vector_len(vec);
	v = vec;
	REF((void *)v.vector);
	i = 0;
	j = 0;
	while ((i < length)) {
		MARK();
		while ((j < (length - i))) {
			MARK();
			if ((ith(v, (j) - 1) > ith(v, ((j + 1)) - 1))) {
				MARK();
				double swap;

				swap = ith(v, (j) - 1);
				set_ith(v, j - 1, ith(v, ((j + 1)) - 1));
				set_ith(v, (j + 1) - 1, swap);
				RELEASE();
			}
			j = (j + 1);
			RELEASE();
		}
		i = (i + 1);
		RELEASE();
	}
	{
		REF((void *)v.vector);
		EXIT();
		DEC((void *)v.vector);
		return v;
	}
	EXIT();
}

int
main(int argc, char *argv[])
{
	setup_error_handlers();
	ENTER();
	VECTOR(x);
	x = Vector_new((double[]) {
				   1, 4, 2, 3}, 4);
	REF((void *)x.vector);
	print_vector(bubbleSort(x));
	EXIT();
	return 0;
}