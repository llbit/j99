/* e2_3.c: non-constant expression in conditional directive */

#if 0++
#error not a valid integer constant expression
#endif
#if 0--
#error not a valid integer constant expression
#endif
#if ++0
#error not a valid integer constant expression
#endif
#if --0
#error not a valid integer constant expression
#endif
