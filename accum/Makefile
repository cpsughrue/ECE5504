########################################################################
# ECE5504 ....
########################################################################

CC := riscv64-unknown-elf-gcc
OBJDUMP := riscv64-unknown-elf-objdump

CFLAGS := -std=gnu11 -Wall -specs=htif_nano.specs

.PHONY: accum_32
accum_32: accum_32.riscv

.PRECIOUS: %.riscv

%_32.riscv: %.c trap.c
	$(CC) $(CFLAGS) -DSIZE=32 -o $@ $< trap.c

.PHONY: clean
clean:
	rm -f -- *.riscv *.o *.dump

.SUFFIXES: # Disable built-in suffix rules
