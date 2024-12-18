### Using custom toolchain

Build:
```
cd ~/rocket/chipyard-clean/toolchains/riscv-tools/riscv-gnu-toolchain
./configure --prefix=~/riscv_custom
make -j$(nproc)
```

Steps to compile:
```
~/riscv_custom/bin/./riscv64-unknown-elf-gcc file.c -o file.riscv
```


Steps to build custom accelerator:
```
cd ~/rocket/chipyard-clean/sims/verilator
make CONFIG=CustomSinRocketConfig -j4
```

### Directory Structure

* chisel-template: implementation of lookup table and chybyshev chisel modules with test harness
* chipyard-accelerators: copy of the modified files from chipyard
* reference-impl: reference implementations of all the algorithms written in C
