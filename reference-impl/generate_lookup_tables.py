import math

def average_spacing(arr):
    differences = [arr[i+1] - arr[i] for i in range(len(arr) - 1)]
    return math.floor(sum(differences) / len(differences))

def largest_spacing(arr):
    differences = [arr[i+1] - arr[i] for i in range(len(arr) - 1)]
    return max(differences)

def generate_sine_table_header(filename):
    PI = math.pi
    N = 29

    # values for sin lookup table
    TABLE_SIZE = 1024
    spacing = PI / (TABLE_SIZE - 1)

    x_values = [-PI / 2 + i * spacing for i in range(TABLE_SIZE)]
    scaled_x_values = [math.floor(i * (2 ** N)) for i in x_values]
    sine_values = [math.floor(math.sin(x) * (2 ** N)) for x in x_values]

    max_value_of_signed_32_bit_int = 2_147_483_647

    # to calculate overflow of lookup table the largest x value could be multiplied by two
    # check that multipling the largest x value still fits within a 32 bit int
    print(f"spare space: {scaled_x_values[-1] * 2 - max_value_of_signed_32_bit_int}")
    print(f"average distance between x elements: {average_spacing(scaled_x_values)}")
    print(f"largest distance between sin elements: {largest_spacing(sine_values)}")

    # arctan values for cordic iteration
    MAX_BITS = 16
    atan_table = [0] * MAX_BITS
    t = 1.0
    for i in range(MAX_BITS):
        atan_table[i] = math.atan(t)
        t /= 2

    with open(filename, "w") as f:
        f.write("#ifndef SIN_LOOKUP_TABLE_H\n")
        f.write("#define SIN_LOOKUP_TABLE_H\n\n")

        f.write(f"#define FIXED_POINT_N {N}\n")
        f.write(f"#define TABLE_SIZE {TABLE_SIZE}\n")

        f.write("static const int32_t sin_table[TABLE_SIZE] = {\n")
        f.write(",\n".join(f"    {value}" for value in sine_values))
        f.write("\n};\n\n")

        f.write("static const int32_t x_table[TABLE_SIZE] = {\n")
        f.write(",\n".join(f"    {value}" for value in scaled_x_values))
        f.write("\n};\n\n")
        
        f.write("// IEEE floats have 23 bit mantissa\n")
        f.write(f"#define MAX_BITS {MAX_BITS}\n")
        f.write("// [arctan(1/2^0), arctan(1/2^1), arctan(1/2^2), arctan(1/2^3), ...]\n")
        f.write("static const float atan_table[MAX_BITS] = {\n")
        f.write(",\n".join(f"    {value:.8f}" for value in atan_table))
        f.write("\n};\n\n")

        f.write("#endif // SIN_LOOKUP_TABLE_H\n")

if __name__ == "__main__":
    filename = "sin_lookup_table.h"

    generate_sine_table_header(filename)
    print(f"Generated lookup table header file: '{filename}'\n")
