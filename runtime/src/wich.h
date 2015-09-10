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
	int refs;    		// refs to this object
} heap_object;

typedef struct {
	heap_object metadata;
	size_t length;      // number of doubles
	double data[];      // a label to the start of the data part of vector
} Vector;

typedef struct string {
	heap_object metadata;
	char str[];
	/* the string starts at the end of fixed fields; this field
	 * does not take any room in the structure; it's really just a
	 * label for the element beyond the length field. So, there is no
	 * need set this field. You must, however, copy strings into it.
	 * You cannot set p->str = "foo";
	 * Must terminate with '\0';
	 */
} String;

String *String_new(char *s);
String *String_from_char(char c);
String *String_alloc(size_t size);
String *String_add(String *s, String *t);
String *String_copy(String *s);
void print_string(String *s);

Vector *Vector_empty();
Vector *Vector_copy(Vector *v);
Vector *Vector_alloc(size_t size);
Vector *Vector_new(double *data, size_t n);
Vector *Vector_append(Vector *a, double value);
Vector *Vector_append_vector(Vector *a, Vector *b);

Vector *Vector_add(Vector *a, Vector *b);
Vector *Vector_sub(Vector *a, Vector *b);
Vector *Vector_mul(Vector *a, Vector *b);
Vector *Vector_div(Vector *a, Vector *b);

char *Vector_as_string(Vector *a);
void print_vector(Vector *a);

void wich_free(heap_object *p);

#define COPY_ON_WRITE(x) \
	if ( x!=NULL && ((heap_object *)x)->refs > 1 ) { \
		((heap_object *)x)->refs--; \
		x = Vector_copy(x); \
		((heap_object *)x)->refs = 1; \
	}

#define REF(x) \
	if ( x!=NULL ) ((heap_object *)x)->refs++; \
	printf("REF(" #x ") bumps refs to %d\n", ((heap_object *)x)->refs);

#define DEREF(x) \
	printf("DEREF(" #x ") has %d refs\n", ((heap_object *)x)->refs);\
	if ( x!=NULL ) ((heap_object *)x)->refs--; \
	if ( x!=NULL && ((heap_object *)x)->refs==0 ) { \
		printf("free(" #x ")\n"); \
        wich_free((heap_object *)x); \
		x = NULL; \
	}
