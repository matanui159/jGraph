#include "output.h"

#include <stdlib.h>

static FILE* output;

static void closeoutput() {
	fclose(output);
}

void setoutput(const char* file) {
	output = fopen(file, "wb");
	atexit(closeoutput);
}

void outbyte(unsigned char b) {
	putc(b, output);
}

void outnumber(double num) {
	fwrite(&num, 8, 1, output);
}

void outstring(const char* str) {
	for (; *str != '\0'; ++str) {
		outbyte(*str);
	}
	outbyte('\0');
}
