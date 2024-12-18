#include "/home/cpsughrue/rocket/chipyard-clean/tests/rocc.h"
#include "stdio.h"
#include "hpm.h"
#include "math.h"
#include "sin_lookup_table.h"

static inline int32_t calculate_sin(int32_t num) {
	int32_t value;
	ROCC_INSTRUCTION_DSS(0, value, 0, (int64_t)num, 1);
	return value;
}

int64_t linear_interpolate(int32_t x0, int32_t y0, int32_t x1, int32_t y1, int32_t x) {
    return y0 + (y1 - y0) * (x - x0) / (x1 - x0);
}

int64_t lookup_sin(int32_t x) {
    int32_t spacing = 1648709;

    int32_t i = (x + 843314857) / spacing;
    
    return linear_interpolate(x_table[i], sin_table[i], x_table[i + 1], sin_table[i + 1], x);
}

int main(int argc, char *argv[])
{

	/* Enable performance counters */
    hpm_init();

	// int32_t data = 421657428; // pi/4 * 2^29
	// int32_t result = 0;
	// for (int i = 0; i < 1000; i++) {
	// 	result = calculate_sin(data);
	// 	// data -= 1000000;
	// }

	int32_t data = 421657428; // pi/4 * 2^29
	int32_t result = 0;
	for (int i = 0; i < 1000; i++) {
		result = lookup_sin(data);
		// data -= 1000000;
	}

	// float data = 0.785398163397f; // pi/4
	// float result = 0;
	// for (int i = 0; i < 1000; i++) {
	// 	result = sin(data);
	// 	// data -= 0.001862645149;
	// }

	/* Print performance counter data */
	hpm_print();

	printf("result: %d\n", result);
	// printf("result: %f\n", result);
	return 0;
}
