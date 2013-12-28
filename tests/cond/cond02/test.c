/* conditional inclusion */

/* identifier that are defined as macros are replaced by zero */

#if zero_replaced
	#error this line should not be included
#else
int a;
#endif
