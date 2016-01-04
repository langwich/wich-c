#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

PVector_ptr bubbleSort(PVector_ptr v);

PVector_ptr bubbleSort(PVector_ptr v)
{
    ENTER();
    int length;
    int i;
    int j;
    REF((void *)v.vector);
    length = Vector_len(v);
    i = 1;
    j = 1;
    while ((i <= length)) {
    	MARK();
        j = 1;
        while ((j <= (length - i))) {
        	MARK();
            if ((ith(v, (j)-1) > ith(v, ((j + 1))-1))) {
            	MARK();
                double swap;
                swap = ith(v, (j)-1);
                set_ith(v, j-1, ith(v, ((j + 1))-1));
                set_ith(v, (j + 1)-1, swap);
                RELEASE();
            }
            j = (j + 1);
            RELEASE();
        }
        i = (i + 1);
        RELEASE();
    }
    {REF((void *)v.vector); EXIT(); DEC((void *)v.vector); return v;}

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	VECTOR(x);
	x = Vector_new((double []){100,99,4,2.15,2,23,3}, 7);
	REF((void *)x.vector);
	print_vector(bubbleSort(PVector_copy(x)));
    EXIT();
	return 0;
}

