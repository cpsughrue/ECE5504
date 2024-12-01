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

  val sinLookupTable = Module(new SinLookupTable)
  val xLookupTable = Module(new XLookupTable)

  // Q5.27 representation of pi/(20 - 1)
  val spacing: SInt = 22192496.S
  val index: SInt = (io.in + 210828715.S) / spacing

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
