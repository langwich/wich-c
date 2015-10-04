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
//#define DEBUG
#include "wich.h"
#include "refcounting.h"

static const int MAX_ROOTS = 1024;
static int sp = -1; // grow upwards; inc then set for push.
static heap_object **roots[MAX_ROOTS];

// There is a general assumption that support routines follow same
// ref counting convention as Wich code: functions REF their heap args and
// then DEREF before returning; it is the responsibility of the caller
// to REF/DEREF heap return values

Vector *Vector_new(double *data, size_t n)
{
	Vector *v = Vector_alloc(n);
	memcpy(v->data, data, n * sizeof(double));
	return v;
}

Vector *Vector_copy(Vector *v)
{
	REF(v);
	Vector *result = Vector_new(v->data, v->length);
	DEREF(v); // might free(v)
	return result;
}

Vector *Vector_empty()
{
	int n = 10;
	Vector *v = Vector_alloc(n);
	memset(v->data, 0, n*sizeof(double));
	return v;
}

Vector *Vector_alloc(size_t size)
{
	Vector *v = wich_malloc(sizeof(Vector) + size * sizeof(double));
	v->metadata.refs = 0;
	v->length = size;
	return v;
}

Vector *Vector_add(Vector *a, Vector *b)
{
	REF(a);
	REF(b);
	int i;
	if ( a==NULL || b==NULL || a->length!=b->length ) return NULL;
	size_t n = a->length;
	Vector * c = Vector_alloc(n);
	for (i=0; i<n; i++) {
		c->data[i] = a->data[i] + b->data[i];
	}
	DEREF(a);
	DEREF(b);
	return c;
}

static char *Vector_as_string(Vector *a) // not called from Wich so no REF/DEREF
{
	char *s = calloc(a->length*20, sizeof(char));
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

void print_vector(Vector *a)
{
	REF(a);
	char *vs = Vector_as_string(a);
	printf("%s\n", vs);
	free(vs);
	DEREF(a);
}

String *String_alloc(size_t size)
{
	String *s = (String *)wich_malloc(sizeof(String) + size * sizeof(char) + 1); // include \0 of string
	s->metadata.refs = 0;
	return s;
}

String *String_new(char *orig)
{
	String *s = String_alloc(strlen(orig));
	strcpy(s->str, orig);
	return s;
}

String *String_from_char(char c)
{
	char buf[2] = {c, '\0'};
	return String_new(buf);
}

void print_string(String *a)
{
	REF(a);
	printf("%s\n", a->str);
	DEREF(a);
}

String *String_add(String *s, String *t)
{
	if ( s==NULL ) return t; // don't REF/DEREF as we might free our return value
	if ( t==NULL ) return s;
	REF(s);
	REF(t);
	size_t n = strlen(s->str) + strlen(t->str);
	String *u = String_alloc(n);
	strcpy(u->str, s->str);
	strcat(u->str, t->str);
	DEREF(s);
	DEREF(t);
	return u;
}

/** We don't have data aggregates like structs so no need to free nested pointers. */
void wich_free(heap_object *p)
{
	free(p);
}

void *wich_malloc(size_t nbytes)
{
	return malloc(nbytes);
}

int _heap_sp() { return sp; }
void _set_sp(int _sp) { sp = _sp; }

/* Announce a heap reference so we can _deref() all before exiting a function */
void _heapvar(heap_object **p) {
	roots[++sp] = p;
}

/* DEREF the stack a to b inclusive */
void _deref(int a, int b) {
#ifdef DEBUG
	printf("deref(%d,%d)\n", a,b);
#endif
	for (int i=a; i<=b; i++) {
		heap_object **addr_of_root = roots[i];
		heap_object *root = *addr_of_root;
#ifdef DEBUG
		printf("deref %p\n", root);
#endif
		DEREF(root);
	}
}
