#include "/home/ramm/rocket/chipyard-clean/tests/rocc.h"
#include "stdio.h"

#define ITERATIONS 50000

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
    unsigned long result;

    /* Enable performance counters */
    hpm_init();

    for (int i; i< ITERATIONS; i++){
        unsigned long start = read_cycles();
        result = calculate_sin(2048);
        unsigned long end = read_cycles();
    }
    /* Print performance counter data */
    hpm_print();

    printf("result is %lu\n", result);
    printf("Cycles taken is %lu\n", (end - start));

	return 0;
}
