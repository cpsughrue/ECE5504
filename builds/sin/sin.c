#include "/home/ramm/rocket/chipyard-clean/tests/rocc.h"
#include "stdio.h"
#include "hpm.h"

static inline unsigned long calculate_sin(int idx)
{
	unsigned long value;
 // ROCC_INSTRUCTION_DSS(X, rd,  rs1, rs2, funct)
	ROCC_INSTRUCTION_DSS(0, value, 0, idx, 1);
	return value;
}

static inline unsigned long read_cycles(void)
{
    unsigned long cycles;
    asm volatile ("rdcycle %0" : "=r" (cycles));
    return cycles;
}

int main(int argc, char *argv[])
{
    hpm_init();
    unsigned long start = read_cycles();
	unsigned long result = calculate_sin(0);
	unsigned long end = read_cycles();
    hpm_print();

    printf("result is %lu\n", result);
    printf("Cycles taken is %lu\n", (end - start));

	return 0;
}
