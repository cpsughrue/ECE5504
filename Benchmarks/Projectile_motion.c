#include <stdio.h>
#include <math.h>

#define G 10.0 // Acceleration due to gravity
#define INITIAL_VELOCITY 30.0 // Fixed initial velocity in m/s

int main() {
    double angle_degrees;
    double angle_radians;
    double time_of_flight, horizontal_range, max_height;

    // Loop through angles from 1° to 90°
    for (angle_degrees = 1; angle_degrees <= 90; angle_degrees++) {
        // Convert the angle to radians
        angle_radians = angle_degrees * M_PI / 180.0;

        // Calculate the time of flight
        // total time of flight by the projectile is given by: t = 2usinθ/g
        time_of_flight = (2 * INITIAL_VELOCITY * sin(angle_radians)) / G;

        // Calculate the horizontal range
        // formula for the horizontal range is: R = u2sin2θ/g.
        horizontal_range = (pow(INITIAL_VELOCITY, 2) * sin(2 * angle_radians)) / G;

        // Calculate the maximum height
        // Maximum height of the projectile is given by the formula:   Hmax = u2sin2θ/2g
        max_height = (pow(INITIAL_VELOCITY * sin(angle_radians), 2)) / (2 * G);

        // Output the results for the current angle
        printf("Angle: %.2f degrees\n", angle_degrees);
        printf("Time of flight: %.2f seconds\n", time_of_flight);
        printf("Horizontal range: %.2f meters\n", horizontal_range);
        printf("Maximum height: %.2f meters\n\n", max_height);
    }

    return 0;
}
