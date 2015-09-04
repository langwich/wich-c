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

typedef struct {
	int refs;           // refs to this object
} heap_object;

typedef struct {
	heap_object metadata;
	int length;         // number of doubles
	double data[];       // a label to the start of the data part of vector
} Vector;

typedef struct string {
	heap_object metadata;
	int length;         // number of char in string
	char str[];
	/* the string starts at the end of fixed fields; this field
     * does not take any room in the structure; it's really just a
     * label for the element beyond the length field. So, there is no
     * need set this field. You must, however, copy strings into it.
     * You cannot set p->str = "foo";
     */
} String;

String *String_new(char *s);
String *String_add(String *s, String *t);
String *String_copy(String *s);

Vector *Vector_empty();
Vector *Vector_alloc(int size);
Vector *Vector_new(double *data, int n);
Vector *Vector_append(Vector *a, double value);
Vector *Vector_append_vector(Vector *a, Vector *b);

Vector *Vector_add(Vector *a, Vector *b);
Vector *Vector_sub(Vector *a, Vector *b);
Vector *Vector_mul(Vector *a, Vector *b);
Vector *Vector_div(Vector *a, Vector *b);

char *Vector_as_string(Vector *a);
