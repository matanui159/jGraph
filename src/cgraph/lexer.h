#ifndef LEXER_H_
#define LEXER_H_

#define TOKEN_END		0
#define TOKEN_NUMBER	1
#define TOKEN_SYMBOL	2
#define TOKEN_NAME		3

typedef struct {
	int type;
	double number;
	char symbol;
	char* name;
} Token;

Token nexttoken();
Token peektoken();

#endif
