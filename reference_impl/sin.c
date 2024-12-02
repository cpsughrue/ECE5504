#include <math.h>
#include <stdint.h>
#include <stdio.h>

#include "range_reduction.h"

/* CHEBYSHEV POLYNOMIAL */

float chebyshev_sin(float x) {

    const float coeffs[] = {
        -0.10132118f,           // x
         0.0066208798f,         // x^3
        -0.00017350505f,        // x^5
         0.0000025222919f,      // x^7
        -0.000000023317787f,    // x^9
         0.00000000013291342f   // x^11
    };

    const float pi_major = 3.1415927f;
    const float pi_minor = -0.00000008742278f;

    float x2 = x * x;

    float p11 = coeffs[5];
    float p9  = p11 * x2 + coeffs[4];
    float p7  = p9  * x2 + coeffs[3];
    float p5  = p7  * x2 + coeffs[2];
    float p3  = p5  * x2 + coeffs[1];
    float p1  = p3  * x2 + coeffs[0];

    return (x - pi_major - pi_minor) * (x + pi_major + pi_minor) * p1 * x;
}

#define N 30 // 31 causes overflow

// Q34.30
int64_t chebyshev_fp_sin(int64_t x) {

    const int64_t coeffs[] = {
        -0.10132118          * pow(2, N),
         0.0066208798        * pow(2, N),
        -0.00017350505       * pow(2, N),
         0.0000025222919     * pow(2, N),
        -0.000000023317787   * pow(2, N),
         0.00000000013291342 * pow(2, N) 
    };

    //const int64_t pi_major = 13816870814000;
    //const int64_t pi_minor = -384489;
    const int64_t pi_major = 3.1415927 * pow(2, N);
    const int64_t pi_minor = -0.00000008742278 * pow(2, N);

    printf("pi_major: %lld\n", pi_major);

    int64_t x2 = (x * x) >> N;

    int64_t p11 = coeffs[5];
    int64_t p9  = ((p11 * x2) >> N) + coeffs[4];
    int64_t p7  = ((p9  * x2) >> N) + coeffs[3];
    int64_t p5  = ((p7  * x2) >> N) + coeffs[2];
    int64_t p3  = ((p5  * x2) >> N) + coeffs[1];
    int64_t p1  = ((p3  * x2) >> N) + coeffs[0];

    int64_t t1 = (x - pi_major - pi_minor) * (x + pi_major + pi_minor) >> N;
    int64_t t2 = (t1 * p1) >> N;
    return  (t2 * x) >> N;
}

/* LOOKUP TABLE */

#include "sin_lookup_table.h"

int64_t linear_interpolate(int64_t x0, int64_t y0, int64_t x1, int64_t y1, int64_t x) {
// int64_t linear_interpolate(int32_t x0, int32_t y0, int32_t x1, int32_t y1, int32_t x) {
    return y0 + (y1 - y0) * (x - x0) / (x1 - x0);
}

int64_t lookup_sin(int32_t x) {
    int32_t spacing = 1648709;

    int32_t i = (x + 843314857) / spacing;
    
    return linear_interpolate(x_table[i], sin_table[i], x_table[i + 1], sin_table[i + 1], x);
}

/* CORDIC */

/*
float getInvGain() {
    // compute gain by evaluating cos(0) without inv gain
    float x, y, z;
    x = 1;
    y = 0;
    z = 0;
    cordic(&x, &y, &z, -1);
    return 1 / x;
}
*/
#define INV_GAIN 0.6072529350088812561694

// cos, sin, x, -1
void cordic(float* x0, float* y0, float* z0, float vecmode) {
    float t;
    float x, y, z;
    int i;
    t = 1;
    x = *x0; y = *y0; z = *z0;
    for (i = 0; i < MAX_BITS; ++i) {
        float x1;
        if (vecmode >= 0 && y < vecmode || vecmode < 0  && z >= 0) {
            x1 = x - y * t;
            y = y + x * t;
            z = z - atan_table[i];
        }
        else {
            x1 = x + y * t;
            y = y - x * t;
            z = z + atan_table[i];
        }
        x = x1;
        t /= 2;
    }
    *x0 = x;
    *y0 = y;
    *z0 = z;
}

float cordic_sin(float x) {
    float sin = 0;
    float cos = INV_GAIN;
    cordic(&cos, &sin, &x, -1);
    return sin;
}

int main () {
    // All values must be range reduced to [-pi/2, pi/2] before being calculated
    float x = 1.57;
    printf("stdlib:    %f\n", sin(x));
    printf("cordic:    %f\n\n", cordic_sin(normalize(x)));    
    
    int64_t value = normalize(x) * pow(2, N);
    int64_t result = chebyshev_fp_sin(value);
    printf("chebyshev float:  %f\n", chebyshev_sin(normalize(x)));
    printf("chebyshev result: %lld\n", result);
    printf("chebyshev fp:     %f\n\n", (float)(result / pow(2, N)));

    int32_t lookup_value = normalize(x) * pow(2, FIXED_POINT_N);
    int64_t lookup_result = lookup_sin(lookup_value);
    printf("lookup:    %f\n", (float)(lookup_result / pow(2, FIXED_POINT_N)));

    return 0;
}
