#include "error.h"

#include "output.h"
#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>

void error(const char* error, ...) {
	putchar('!');
	va_list list;
	va_start(list, error);
	vprintf(error, list);
	va_end(list);
	putchar('\n');
	fflush(stdout);
	exit(1);
}

void unexpected(Token t) {
	switch (t.type) {
	case TOKEN_END:
		error("Unexpected end of expression");
	case TOKEN_NUMBER:
		error("Unexpected number '%g'", t.number);
	case TOKEN_SYMBOL:
		error("Unexpected symbol '%c'", t.symbol);
	case TOKEN_NAME:
		error("Unexpected name '%s'", t.name);
	}
}
