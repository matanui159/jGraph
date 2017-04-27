#ifndef OUTPUT_H_
#define OUTPUT_H_

#include <stdio.h>

void setoutput(const char* file);
void outbyte(unsigned char b);
void outnumber(double num);
void outstring(const char* str);

#endif
