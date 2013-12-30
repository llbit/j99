/* you can't have a comment in a char literal, but it should still be parsed
   as a char literal */
char c1 = '/* not a comment */';
char c2 = '// not a comment';
