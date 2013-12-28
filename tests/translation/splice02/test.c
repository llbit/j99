/* line splicing */

// the following three lines are spliced together:
int line_\
spliced = /* comment */\
1;

int not_\/* If \ is not followed directly by newline
			 the line will not be spliced. This comment
			 is replaced by one space character. */
spliced_1;

int not_\// this is not a line splice either (comment replaced by ' ')
spliced_2;

