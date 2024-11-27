// ~/rocket/chipyard-clean/generators/rocket-chip/src/main/scala/tile/accelerator.scala

package freechips.rocketchip.tile

import chisel3._
import chisel3.util._
import freechips.rocketchip.config.Parameters


class CustomSinAccelerator(opcodes: OpcodeSet)(implicit p: Parameters) extends LazyRoCC(opcodes = opcodes) {
  override lazy val module = new CustomSinAcceleratorModule(this)
}

class CustomSinAcceleratorModule(outer: CustomSinAccelerator) extends LazyRoCCModuleImp(outer) {
  val cmd = io.cmd
  val funct = cmd.bits.inst.funct
  val rs1 = cmd.bits.rs1
  val rs2 = cmd.bits.rs2

  when(io.cmd.fire()) {
    printf("Custom opcode received: funct=%d, rs1=%d, rs2=%d\n", funct, rs1, rs2)
  }

  // Implement your sine computation function inside the class
  def computeSin(input: UInt): UInt = {
    // Your sine computation logic
    input // Placeholder, replace with actual computation
  }
}
