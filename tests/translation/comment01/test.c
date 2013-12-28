/* trigraphs, comments and line splicing */

/*
 * This comment will end on the next line due
 * to the trigraph which causes line splicing and
 * thus merging of the * and / to form the end of the
 * comment.
 *
 * This tests that comment replacement occurs
 * after line splicing, as it should. And that
 * line splicing occurs after trigraph replacement.
 *
 *??/
/
int first;
//\
int bad; ** this is all part of the previous line comment **
int second;
