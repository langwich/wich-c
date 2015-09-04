/*
The MIT License (MIT)

Copyright (c) 2015 Terence Parr, Hanzhou Shi, Shuai Yuan, Yuanyuan Zhang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "wich.h"

Vector *Vector_new(double *data, int n)
{
	Vector *v = Vector_alloc(n);
	memcpy(v->data, data, n * sizeof(double));
	return v;
}

Vector *Vector_empty()
{
	int n = 10;
    Vector *v = Vector_alloc(n);
	memset(v->data, 0, n*sizeof(double));
	return v;
}

Vector *Vector_alloc(int size)
{
    Vector *v = malloc(sizeof(Vector) + size * sizeof(double));
    v->length = size;
    return v;
}

char *Vector_as_string(Vector *a)
{
	char *s = calloc((size_t)a->length*20, sizeof(char));
	char buf[50];
	strcat(s, "[");
	for (int i=0; i<a->length; i++) {
		if ( i>0 ) strcat(s, ", ");
		sprintf(buf, "%1.2f", a->data[i]);
		strcat(s, buf);
	}
	strcat(s, "]");
	return s;
}

String *String_new(char *orig)
{
    size_t n = strlen(orig);
    String *s = (String *) malloc(sizeof(String) + n * sizeof(char) + 1); // include \0 of string
    s->length = (int)n;
    memset(s->str, 0, n*sizeof(char));
    return s;
}

/* old stuff we might use as base for double vector?

void IntList_add(IntList *list, int v) {
    if ( list->next >= list->size ) {
        int *old = list->data;
        list->data = calloc(list->size*2, sizeof(int));
        memcpy(list->data, old, list->size);
        free(old);
        list->size *= 2;
    }
    list->data[list->next++] = v;
}

// Do two IntLists have same elements?
int IntList_eq(IntList a, IntList b) {
    int i;
    if ( a.next!=b.next ) return 0;
    int n = a.next > b.next ? a.next : b.next; // get max
    if ( n<=0 ) return 0;
    for (i=0; i<n; i++) {
        if ( a.data[i]!=b.data[i] ) return 0;
    }
    return 1;
}
*/