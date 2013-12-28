/* p4.c: macro redefinition */

#define FIRST 1
int main(int argc, char** argv)
{
	#define FIRST 2

	assert(FIRST == 1);

	#undef FIRST
	#define FIRST 2

	assert(FIRST == 2);

	return 0;
}
