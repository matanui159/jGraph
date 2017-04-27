#include "lexer.h"

#include "input.h"
#include "error.h"
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <math.h>

#define TOKEN_UNDEFINED	(-1)

#define STATE_ABS	0
#define STATE_FLT	1
#define STATE_EXP	2

typedef unsigned long ulong;

static Token current = {.type = TOKEN_UNDEFINED};

static char* name = NULL;
static int size = 0;
static int capacity = 0;

static int isnumber(char c) {
	return c && strchr("0123456789.eE", c);
}

static int issymbol(char c) {
	return c && strchr("+-%*/^()|", c);
}

static int isnamestart(char c) {
	return c && (isalpha(c) || strchr("$_", c));
}

static int isname(char c) {
	return isnamestart(c) || isdigit(c);
}

static double nextnumber() {
	ulong abs = 0;
	ulong flt = 0;
	ulong exp = 0;
	ulong div = 1;
	int state = STATE_ABS;

	while (isnumber(peekchar())) {
		char c = nextchar();
		if (c == '.') {
			if (state == STATE_ABS) {
				state = STATE_FLT;
			} else {
				UNEXPECTED_CHAR(c);
			}
		} else if (c == 'e' || c == 'E') {
			if (state == STATE_EXP) {
				UNEXPECTED_CHAR(c);
			} else {
				state = STATE_EXP;
			}
		} else {
			switch (state) {
			case STATE_ABS:
				abs = abs * 10 + (c - '0');
				break;
			case STATE_FLT:
				flt = flt * 10 + (c - '0');
				div *= 10;
				break;
			case STATE_EXP:
				exp = exp * 10 + (c - '0');
				break;
			}
		}
	}
	return (abs + ((double)flt / div)) * pow(10, exp);
}

static void namecleanup() {
	free(name);
}

static void appendchar(char c) {
	++size;
	if (size > capacity) {
		capacity = capacity ? capacity * 2 : 1;
		name = realloc(name, capacity);
		if (capacity == 1) {
			atexit(namecleanup);
		}
	}
	name[size - 1] = c;
}

static char* nextname() {
	size = 0;
	char c;
	while (isname(c = peekchar())) {
		appendchar(nextchar());
	}
	appendchar('\0');
	return name;
}

Token nexttoken() {
	Token t = peektoken();
	current.type = TOKEN_UNDEFINED;
	return t;
}

Token peektoken() {
	if (current.type == TOKEN_UNDEFINED) {
		while (isspace(peekchar())) nextchar();
		char c = peekchar();
		if (c == '\0') {
			current.type = TOKEN_END;
			nextchar();
		} else if (isdigit(c) || c == '.') {
			current.type = TOKEN_NUMBER;
			current.number = nextnumber();
		} else if (issymbol(c)) {
			current.type = TOKEN_SYMBOL;
			current.symbol = nextchar();
		} else if (isnamestart(c)) {
			current.type = TOKEN_NAME;
			current.name = nextname();
		} else {
			INVALID_CHAR(c);
		}
	}
	return current;
}
