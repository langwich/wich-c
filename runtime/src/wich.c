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
#include "wich.h"

Vector *Vector_empty()
{
    return Vector_new(10);
}

Vector *Vector_new(int size)
{
    Vector *v = malloc(sizeof(Vector) + size * sizeof(float));
    v->length = size;
    memset(v->data, 0, size*sizeof(float));
    return v;
}

String *String_new(char *s);
String *String_add(String *s, String *t);
String *String_copy(String *s);

String *String_new(char *orig)
{
    size_t n = strlen(orig);
    String *s = (String *) malloc(sizeof(String) + n * sizeof(char) + 1); // include \0 of string
    s->length = (int)n;
    memset(s->str, 0, n*sizeof(char));
    return s;
}

/* old stuff we might use as base for float vector?

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