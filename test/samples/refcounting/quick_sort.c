#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

PVector_ptr quickSort(PVector_ptr nums,int lo,int hi);

PVector_ptr quickSort(PVector_ptr nums,int lo,int hi)
{
    ENTER();
    int p;
    double x;
    int j;
    int i;
    double temp;
    VECTOR(l);
    VECTOR(r);
    REF((void *)nums.vector);
    if ((lo >= hi)) {
    	MARK();
        {REF((void *)nums.vector); EXIT(); DEC((void *)nums.vector); return nums;}
        RELEASE();
    }
    p = 0;
    x = ith(nums, (hi)-1);
    j = lo;
    i = (lo - 1);
    while ((j < hi)) {
    	MARK();
        if ((ith(nums, (j)-1) <= x)) {
        	MARK();
            double temp;
            i = (i + 1);
            temp = ith(nums, (j)-1);
            set_ith(nums, j-1, ith(nums, (i)-1));
            set_ith(nums, i-1, temp);
            RELEASE();
        }
        j = (j + 1);
        RELEASE();
    }
    p = (i + 1);
    temp = ith(nums, (p)-1);
    set_ith(nums, p-1, ith(nums, (hi)-1));
    set_ith(nums, hi-1, temp);
    l = PVector_copy(quickSort(PVector_copy(nums),lo,(p - 1)));
    REF((void *)l.vector);
    r = PVector_copy(quickSort(PVector_copy(l),(p + 1),hi));
    REF((void *)r.vector);
    {REF((void *)r.vector); EXIT(); DEC((void *)r.vector); return r;}

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	VECTOR(v);
	v = Vector_new((double []){1,3,4,5,2,100,99,0,-1}, 9);
	REF((void *)v.vector);
	print_vector(quickSort(PVector_copy(v),1,9));
    EXIT();
	return 0;
}

