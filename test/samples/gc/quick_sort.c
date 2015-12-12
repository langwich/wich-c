#include <stdio.h>
#include "wich.h"
#include "gc.h"

PVector_ptr quickSort(PVector_ptr nums,int lo,int hi);

PVector_ptr quickSort(PVector_ptr nums,int lo,int hi)
{
	gc_begin_func();
	int p;
	double x;
	int j;
	int i;
	double temp;
	VECTOR(l);
	VECTOR(r);
	if ((lo >= hi)) {
		{gc_end_func(); return nums;}
	}
	p = 0;
	x = ith(nums, (hi)-1);
	j = lo;
	i = (lo - 1);
	while ((j < hi)) {
		if ((ith(nums, (j)-1) <= x)) {
			double temp;
			i = (i + 1);
			temp = ith(nums, (j)-1);
			set_ith(nums, j-1, ith(nums, (i)-1));
			set_ith(nums, i-1, temp);
		}
		j = (j + 1);
	}
	p = (i + 1);
	temp = ith(nums, (p)-1);
	set_ith(nums, p-1, ith(nums, (hi)-1));
	set_ith(nums, hi-1, temp);
	l = PVector_copy(quickSort(PVector_copy(nums),lo,(p - 1)));
	r = PVector_copy(quickSort(PVector_copy(l),(p + 1),hi));
	{gc_end_func(); return r;}

	gc_end_func();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	gc_begin_func();
	VECTOR(v);
	v = Vector_new((double []){1,3,4,5,2,100,99,0,-1}, 9);
	print_vector(quickSort(PVector_copy(v),1,9));
	gc_end_func();

	gc();
	Heap_Info info = get_heap_info();
	if ( info.live!=0 ) fprintf(stderr, "%d objects remain after collection\n", info.live);
	gc_shutdown();
	return 0;
}

