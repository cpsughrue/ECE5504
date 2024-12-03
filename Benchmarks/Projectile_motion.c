#include <stdio.h>
#include "../builds/sin/hpm.h"
#include "/home/ramm/rocket/chipyard-clean/tests/rocc.h"
#include <math.h>

#define HARDWARE_ACC_SINE 0

#define G 10.0 // Acceleration due to gravity
#define INITIAL_VELOCITY 30.0 // Fixed initial velocity in m/s

#if (HARDWARE_ACC_SINE == 0)
#define SIN(X) sin(X)
#else
#define CONVERSION_FACTOR 536870912 //2^29 

static inline unsigned long sin_custom(double idx)
{
	unsigned long value;
    idx = (int32_t)(idx * CONVERSION_FACTOR);
    // printf("Value given to sin is %f\n", idx);
 // ROCC_INSTRUCTION_DSS(X, rd,  rs1, rs2, funct)
	ROCC_INSTRUCTION_DSS(0, value, 0, (int64_t)idx, 2);
    // printf("Value returned by sin is %lu\n", value);
	return value;
}
#endif

int main() {
    double angle_degrees;
    double angle_radians;
    double time_of_flight, horizontal_range, max_height;
    double sin_val;
    double sin_2val;

    hpm_init();
    // Loop through angles from 1° to 44°
    for (angle_degrees = 1; angle_degrees <= 44; angle_degrees++) {
        // Convert the angle to radians
        angle_radians = angle_degrees * M_PI / 180.0;

        sin_val = (double)sin_custom(angle_radians)/(int32_t)(CONVERSION_FACTOR) ;

        sin_2val = (double)sin_custom(2 * angle_radians)/(int32_t)CONVERSION_FACTOR ;
        // printf("Sin val %d, sin 2 val %d\n", (int32_t)sin_val, (int32_t)sin_2val);

        // Calculate the time of flight
        // total time of flight by the projectile is given by: 
        // t = 2usinθ/g
        time_of_flight = (2 * INITIAL_VELOCITY * sin_val) / G;

        // Calculate the horizontal range
        // formula for the horizontal range is: 
        // R = u2sin2θ/g.
        horizontal_range = (pow(INITIAL_VELOCITY, 2) * sin_2val) / G;

        // Calculate the maximum height
        // Maximum height of the projectile is given by the formula:   
        // Hmax = u2sin2θ/2g
        max_height = (pow(INITIAL_VELOCITY * sin_val, 2)) / (2 * G);

        // Output the results for the current angle
        printf("Angle: %d degrees\n", (int)angle_degrees);
        printf("Time of flight: %d seconds\n", (int32_t)time_of_flight);
        printf("Horizontal range: %d meters\n", (int32_t)horizontal_range);
        printf("Maximum height: %d meters\n\n", (int32_t)max_height);
    }
    hpm_print();
    return 0;
}
