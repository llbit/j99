#ifndef _JLIBC_ASSERT
#define _JLIBC_ASSERT

#include <stdio.h>

# ifdef NDEBUG
	#define assert(ignore) ((void)0)
# else
	#define assert(exp) (exp ? (void)0 : fprintf(stderr, "%s: %d: \"%s\" is zero", __FILE__, __LINE__, __func__, #exp))
# endif

#endif
