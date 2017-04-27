#ifndef ERROR_H_
#define ERROR_H_

#include "lexer.h"

void error(const char* error, ...) __attribute__((noreturn));
void unexpected(Token t);

#define INVALID_USAGE() error("Usage: cgraph [output file] [expression]")
#define INVALID_CHAR(c) error("Invalid character '%c'", c);
#define INVALID_FUNC(name) error("Invalid function '%s'", name)
#define UNEXPECTED_CHAR(c) error("Unexpected character '%c'", c)
#define UNEXPECTED(t) unexpected(t)

#endif
