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

/* Announce a heap reference so we can _deref() all before exiting a function */
void _heapvar(heap_object **p);
void _deref(int,int);
int _heap_sp();
void _set_sp(int);

// enter/exit a function
#define ENTER()		int _funcsp = _heap_sp();// printf("ENTER; sp=%d\n", _funcsp);
#define EXIT()		{/*printf("EXIT; sp=%d\n", _heap_sp());*/ _deref(_funcsp+1,_heap_sp()); _set_sp(_funcsp);}

// enter/exit a block
#define MARK()		int _savesp = _heap_sp();
#define RELEASE()	{_deref(_savesp+1,_heap_sp()); _set_sp(_savesp);}

#define STRING(s)	String *s; _heapvar((heap_object **)&s);
#define VECTOR(v)	Vector *v; _heapvar((heap_object **)&v);

static inline void REF(void *x) {
	if ( x!=NULL ) ((heap_object *)x)->refs++;
#ifdef DEBUG
	printf("REF(%p) bumps refs to %d\n", x, ((heap_object *)x)->refs);
#endif
}

/* A DEREF w/o free'ing even if count goes to zero; used for returning pointers */
static inline void DEC(void *x) {
	if ( x!=NULL ) {
#ifdef DEBUG
		printf("DEC(%p) has %d refs\n", x, ((heap_object *)x)->refs);
#endif
		((heap_object *)x)->refs--;
	}
}

static inline void DEREF(void *x) {
	if ( x!=NULL ) {
#ifdef DEBUG
		printf("DEREF(%p) has %d refs\n", x, ((heap_object *)x)->refs);
#endif
		((heap_object *)x)->refs--;
		if ( ((heap_object *)x)->refs==0 ) {
#ifdef DEBUG
			printf("free(%p)\n", x);
#endif
			wich_free((heap_object *)x);

			x = NULL;
		}
	}
}
