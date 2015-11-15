#include <stdio.h>
#include "wich.h"

bool str_gt(String * s1,String * t);
void gt_msg(String * s,String * t);
void le_msg(String * s,String * t);

bool str_gt(String * s1,String * t)
{
    return String_gt(s1,t);

}

void gt_msg(String * s,String * t)
{
    print_string(String_add(String_add(s,String_new(" is greater than ")),t));

}

void le_msg(String * s,String * t)
{
    print_string(String_add(String_add(s,String_new(" is less than or equal to ")),t));

}


int main(int ____c, char *____v[])
{
	setup_error_handlers();
	String * s1;
	String * s2;
	String * t;
	bool s1t;
	bool s2t;
	s1 = String_new("hellp");
	s2 = String_new("aello");
	t = String_new("hello");
	s1t = str_gt(s1,t);
	if (s1t) {
	    gt_msg(s1,t);
	}
	else {
	    le_msg(s1,t);
	}
	s2t = str_gt(s2,t);
	if (s2t) {
	    gt_msg(s2,t);
	}
	else {
	    le_msg(s2,t);
	}
	return 0;
}

