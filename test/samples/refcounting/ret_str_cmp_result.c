#include <stdio.h>
#include "wich.h"
#include "refcounting.h"

bool str_gt(String * s1,String * t);
void gt_msg(String * s,String * t);
void le_msg(String * s,String * t);

bool str_gt(String * s1,String * t)
{
    ENTER();
    REF((void *)t);
    REF((void *)s1);
    {EXIT(); return String_gt(s1,t);}

    EXIT();
}

void gt_msg(String * s,String * t)
{
    ENTER();
    REF((void *)t);
    REF((void *)s);
    print_string(String_add(String_add(s,String_new(" is greater than ")),t));

    EXIT();
}

void le_msg(String * s,String * t)
{
    ENTER();
    REF((void *)t);
    REF((void *)s);
    print_string(String_add(String_add(s,String_new(" is less than or equal to ")),t));

    EXIT();
}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
    ENTER();
	STRING(s1);
	STRING(s2);
	STRING(t);
	bool s1t;
	bool s2t;
	s1 = String_new("hellp");
	REF((void *)s1);
	s2 = String_new("aello");
	REF((void *)s2);
	t = String_new("hello");
	REF((void *)t);
	s1t = str_gt(s1,t);
	if (s1t) {
		MARK();
	    gt_msg(s1,t);
	    RELEASE();
	}
	else {
		MARK();
	    le_msg(s1,t);
	    RELEASE();
	}
	s2t = str_gt(s2,t);
	if (s2t) {
		MARK();
	    gt_msg(s2,t);
	    RELEASE();
	}
	else {
		MARK();
	    le_msg(s2,t);
	    RELEASE();
	}
    EXIT();
	return 0;
}

