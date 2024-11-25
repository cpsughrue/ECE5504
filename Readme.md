Build:
```
cd riscv-gnu-toolchain
./configure --prefix=~/riscv_custom
make -j$(nproc)
```

Steps to compile:
```
~/riscv_custom/bin/./riscv64-unknown-elf-gcc file.c -o file.riscv
```