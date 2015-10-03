#include <stdio.h>
#include <stdlib.h>
#include <execinfo.h>

void f(uint64_t x, uint64_t y) {
	uint64_t a = 0x9999999;
	char *p = (char *)0x1212121212;
	void **frame = __builtin_frame_address(0);
	printf("frame %p\n", frame);
	printf("frame[0] %p\n", frame[0]);
	printf("frame[-1] %p\n", frame[-1]);
	printf("frame[-2] %p\n", frame[-2]);
	printf("frame[-3] %p\n", frame[-3]);
	printf("frame[-4] %p\n", frame[-4]);
	printf("frame[-5] %p\n", frame[-5]); // should be the local variable called frame
}

int main(int argc, char *argv[]) {
	f(0x43434343, 0xCAFECAFE);
}