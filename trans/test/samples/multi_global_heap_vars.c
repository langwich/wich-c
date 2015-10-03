#include <stdio.h>
#include "wich.h"

int main(int argc, char *argv[])
{
    void *_localptrs[3];

    _localptrs[0] = String_new("hi");
    _localptrs[1] = Vector_new((double[]) {1, 2}, 2);
    _localptrs[2] = Vector_mul(_localptrs[1], 4);
    print_vector(_localptrs[2]);
    return 0;
}
