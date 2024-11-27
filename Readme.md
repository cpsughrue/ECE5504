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