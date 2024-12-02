// See README.md for license details.

package sin

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

class LookupSinModule extends Module {
  val io = IO(new Bundle {
    val in = Input(SInt(32.W))
    val out = Output(SInt(32.W))
  })

  // TODO: Implement range reduction
  assert(io.in > -843314857.S, "input value is too small. must be greater then -pi/2 * 2^29")
  assert(io.in < 843314856.S, "input value is too large. must be smaller then pi/2 * 2^29")

  val sinLookupTable = Module(new SinLookupTable)
  val xLookupTable = Module(new XLookupTable)

  // Information manually pulled from lookup table
  val spacing: SInt = 1648709.S
  val index: SInt = (io.in + 843314857.S) / spacing

  printf("index: %d\n", index)

  sinLookupTable.io.index0 := index
  sinLookupTable.io.index1 := index + 1.S
  val sinOut0: SInt = sinLookupTable.io.out0
  val sinOut1: SInt = sinLookupTable.io.out1

  xLookupTable.io.index0 := index
  xLookupTable.io.index1 := index + 1.S
  val xOut0: SInt = xLookupTable.io.out0
  val xOut1: SInt = xLookupTable.io.out1
  
  val result = LinearInterpolate(xOut0, sinOut0, xOut1, sinOut1, io.in)

  printf("linear interpolation result: %d\n", result)

  io.out := result
}
