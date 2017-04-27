#include "input.h"

#define CHAR_UNDEFINED	((char)0xFF)

#include <stdio.h>

static const char* input;
static int index;
static char current = CHAR_UNDEFINED;

void setinput(const char* in) {
	input = in;
}

char nextchar() {
	char c = peekchar();
	current = CHAR_UNDEFINED;
	return c;
}

char peekchar() {
	if (current == CHAR_UNDEFINED) {
		current = input[index++];
	}
	return current;
}
