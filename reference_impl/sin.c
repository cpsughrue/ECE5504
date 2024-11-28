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

/* LOOKUP TABLE */

#include "sin_lookup_table.h"

float linear_interpolate(float x0, float y0, float x1, float y1, float x) {
    return y0 + (y1 - y0) * (x - x0) / (x1 - x0);
}

float lookup_sin(float x) {
    float spacing = M_PI / (TABLE_SIZE - 1);

    int i = (x + M_PI_2) / spacing;

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
    float x = 53;
    printf("stdlib:    %f\n", sin(x));
    printf("chebyshev: %f\n", chebyshev_sin(normalize(x)));
    printf("loohup:    %f\n", lookup_sin(normalize(x)));
    printf("cordic:    %f\n", cordic_sin(normalize(x)));    

    return 0;
}
