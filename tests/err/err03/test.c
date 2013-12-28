/* e2_2.c: non-constant expression in conditional directive */

#if 0 = 0
#error not a valid integer constant expression
#endif
