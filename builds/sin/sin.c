#include "/home/cpsughrue/rocket/chipyard-clean/tests/rocc.h"
#include "stdio.h"

static inline unsigned long calculate_sin(int idx)
{
	unsigned long value;
 // ROCC_INSTRUCTION_DSS(X, rd,  rs1, rs2, funct)
	ROCC_INSTRUCTION_DSS(0, value, 0, idx, 1);
	return value;
}

int main(int argc, char *argv[])
{
	unsigned long result = calculate_sin(222);

    printf("result is %lu\n", result);

	return 0;
}
