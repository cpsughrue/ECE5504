import math

def generate_sine_table_header(filename):
    PI = math.pi
    
    # values for sin lookup table
    TABLE_SIZE = 1024
    spacing = PI / (TABLE_SIZE - 1)
    x_values = [-PI / 2 + i * spacing for i in range(TABLE_SIZE)]
    sine_values = [math.sin(x) for x in x_values]

    # arctan values for cordic iteration
    MAX_BITS = 23
    atan_table = [0] * MAX_BITS
    t = 1.0
    for i in range(MAX_BITS):
        atan_table[i] = math.atan(t)
        t /= 2

    with open(filename, "w") as f:
        f.write("#ifndef SIN_LOOKUP_TABLE_H\n")
        f.write("#define SIN_LOOKUP_TABLE_H\n\n")

        f.write(f"#define TABLE_SIZE {TABLE_SIZE}\n")

        f.write("static const float sin_table[TABLE_SIZE] = {\n")
        f.write(",\n".join(f"    {value:.8f}" for value in sine_values))
        f.write("\n};\n\n")

        f.write("static const float x_table[TABLE_SIZE] = {\n")
        f.write(",\n".join(f"    {value:.8f}" for value in x_values))
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
    print(f"Generated lookup table header file: '{filename}'")
