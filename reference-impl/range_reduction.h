#include <math.h>

float max(float a, float b) {
    if (a > b)
        return a;
    return b;
}

float min(float a, float b) {
    if (a < b)
        return a;
    return b;
}

float normalize(float x) {
    const float TwoPi = 2 * M_PI;

    // reduce to [-2pi, 2pi]
    x = x - floor(x / TwoPi) * TwoPi;

    // reduce to [-pi/2, pi/2]
    x = min(x, M_PI - x);
    x = max(x, -M_PI - x);
    x = min(x, M_PI - x);

    return x;
}
