#include "input.h"
#include "parser.h"
#include "output.h"
#include "error.h"
#include <stdio.h>

int main(int argc, char* argv[]) {
	if (argc != 3) {
		INVALID_USAGE();
	}
	setoutput(argv[1]);
	setinput(argv[2]);

	outbyte(0x8B);
	outstring(argv[2]);
	parse();

	puts("Compilation Successful!");
	fflush(stdout);
	return 0;
}
