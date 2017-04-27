#include "parser.h"

#include "lexer.h"
#include "output.h"
#include "error.h"
#include <stdlib.h>
#include <string.h>
#include <math.h>

#define CMD_PUSH	0x00
#define CMD_PUSHX	0x01

#define CMD_NEG		0x40
#define CMD_ABS		0x41

#define CMD_ADD		0x80
#define CMD_SUB		0x81
#define CMD_MOD		0x82
#define CMD_MUL		0x83
#define CMD_DIV		0x84
#define CMD_POW		0x85

#define CMD_SQRT	0xC0
#define CMD_LOG		0xC1
#define CMD_LN		0xC2
#define CMD_SIN		0xC3
#define CMD_COS		0xC4
#define CMD_TAN		0xC5
#define CMD_ASIN	0xC6
#define CMD_ACOS	0xC7
#define CMD_ATAN	0xC8
#define CMD_CALL	0xFF

#define VAL_PI 		3.1415926535897932384626433832795
#define VAL_E		2.7182818284590452353602874713527

static int getprec(char c) {
	switch (c) {
	case '+': return 1;
	case '-': return 1;
	case '%': return 2;
	case '*': return 3;
	case '/': return 3;
	case '^': return 4;
	default:  return 0;
	}
}

static void parse_expression(int prec);

static void parse_symbol(char c) {
	Token t = nexttoken();
	if (t.type != TOKEN_SYMBOL || t.symbol != c) {
		UNEXPECTED(t);
	}
}

static void parse_value() {
	Token t = nexttoken();
	switch (t.type) {
	case TOKEN_NUMBER:
		outbyte(CMD_PUSH);
		outnumber(t.number);
		break;
	case TOKEN_SYMBOL:
		switch (t.symbol) {
		case '(':
			parse_expression(0);
			parse_symbol(')');
			break;
		case '|':
			parse_expression(0);
			parse_symbol('|');
			outbyte(CMD_ABS);
		}
		break;
	case TOKEN_NAME:
		if (t.name[0] == '$') {
			char* name = strdup(t.name + 1);
			parse_value();
			outbyte(CMD_CALL);
			outstring(name);
			free(name);
		} else if (strcmp(t.name, "sqrt") == 0) {
			parse_value();
			outbyte(CMD_SQRT);
		} else if (strcmp(t.name, "log") == 0) {
			parse_value();
			outbyte(CMD_LOG);
		} else if (strcmp(t.name, "ln") == 0) {
			parse_value();
			outbyte(CMD_LN);
		} else if (strcmp(t.name, "sin") == 0) {
			parse_value();
			outbyte(CMD_SIN);
		} else if (strcmp(t.name, "cos") == 0) {
			parse_value();
			outbyte(CMD_COS);
		} else if (strcmp(t.name, "tan") == 0) {
			parse_value();
			outbyte(CMD_TAN);
		} else if (strcmp(t.name, "asin") == 0) {
			parse_value();
			outbyte(CMD_ASIN);
		} else if (strcmp(t.name, "acos") == 0) {
			parse_value();
			outbyte(CMD_ACOS);
		} else if (strcmp(t.name, "atan") == 0) {
			parse_value();
			outbyte(CMD_ATAN);
		} else if (strcmp(t.name, "PI") == 0) {
			outbyte(CMD_PUSH);
			outnumber(VAL_PI);
		} else if (strcmp(t.name, "e") == 0) {
			outbyte(CMD_PUSH);
			outnumber(VAL_E);
		} else if (strcmp(t.name, "x") == 0) {
			outbyte(CMD_PUSHX);
		} else {
			INVALID_FUNC(t.name);
		}
		break;
	default:
		UNEXPECTED(t);
	}
}

static void parse_unary() {
	Token t = peektoken();
	if (t.type == TOKEN_SYMBOL && (t.symbol == '+' || t.symbol == '-')) {
		nexttoken();
		parse_value();
		if (t.symbol == '-') {
			outbyte(CMD_NEG);
		}
	}
	parse_value();
}

static void parse_binary(int prec) {
	Token t = peektoken();
	if (t.type == TOKEN_SYMBOL) {
		int next_prec = getprec(t.symbol);
		if (next_prec > prec) {
			nexttoken();
			parse_expression(next_prec);
			switch (t.symbol) {
			case '+':
				outbyte(CMD_ADD);
				break;
			case '-':
				outbyte(CMD_SUB);
				break;
			case '%':
				outbyte(CMD_MOD);
				break;
			case '*':
				outbyte(CMD_MUL);
				break;
			case '/':
				outbyte(CMD_DIV);
				break;
			case '^':
				outbyte(CMD_POW);
				break;
			}
			parse_binary(prec);
		}
	}
}

static void parse_expression(int prec) {
	parse_unary();
	parse_binary(prec);
}

void parse() {
	parse_expression(0);
	if (peektoken().type != TOKEN_END) {
		UNEXPECTED(nexttoken());
	}
}
