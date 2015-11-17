#include <stdio.h>
#include "wich.h"
#include "gc.h"
PVector_ptr bubbleSort(PVector_ptr vec);

PVector_ptr
bubbleSort(PVector_ptr vec)
{
	gc_begin_func();
	int length;

	VECTOR(v);
	int i;

	int j;

	length = Vector_len(vec);
	v = vec;
	i = 0;
	j = 0;
	while ((i < length)) {
		while ((j < (length - i))) {
			if ((ith(v, (j) - 1) > ith(v, ((j + 1)) - 1))) {
				double swap;

				swap = ith(v, (j) - 1);
				set_ith(v, j - 1, ith(v, ((j + 1)) - 1));
				set_ith(v, (j + 1) - 1, swap);
			}
			j = (j + 1);
		}
		i = (i + 1);
	}
	{
		gc_end_func();
		return v;
	}
	gc_end_func();
}

int
main(int argc, char *argv[])
{
	setup_error_handlers();
	gc_begin_func();
	VECTOR(x);
	x = Vector_new((double[]) {
				   1, 4, 2, 3}, 4);
	print_vector(bubbleSort(x));
	gc_end_func();
	gc();
	Heap_Info info = get_heap_info();

	if (info.live != 0)
		fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}
