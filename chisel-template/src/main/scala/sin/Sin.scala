// See README.md for license details.

package sin

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._

class ChebyshevSinModule extends Module {
  val io = IO(new Bundle {
    val in = Input(SInt(64.W))
    val out = Output(SInt(64.W))
  })

  // TODO: Implement range reduction
  assert(io.in > -843314857.S, "input value is too small. must be greater then -pi/2 * 2^29")
  assert(io.in < 843314856.S, "input value is too large. must be smaller then pi/2 * 2^29")

  val N = 29
  val coeffs = VecInit(Seq(
      -54396394.S(64.W),
       3554557.S(64.W),
      -93149.S(64.W),
       1354.S(64.W),
      -12.S(64.W)
  ))

  val pi_major = 1686629738.S(64.W)
  val pi_minor = -46.S(64.W)

  val x2 = (io.in * io.in) >> N

  val p9 = coeffs(4)
  val p7 = ((p9 * x2) >> N) + coeffs(3)
  val p5 = ((p7 * x2) >> N) + coeffs(2)
  val p3 = ((p5 * x2) >> N) + coeffs(1)
  val p1 = ((p3 * x2) >> N) + coeffs(0)    

  val t1 = (((io.in - pi_major - pi_minor) * (io.in + pi_major + pi_minor)) >> N)
  val t2 = (t1 * p1) >> N
  io.out := (t2 * io.in) >> N
}




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
