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



class CordicSinModule extends Module {
  val io = IO(new Bundle {
    val in = Input(SInt(64.W))       // Input angle in fixed-point format
    val resetRuntime = Input(Bool()) // Runtime reset signal
    val out = Output(SInt(64.W))     // Output sine value in fixed-point format
    val done = Output(Bool())        // Indicates when the computation is done
  })

  // TODO: Implement range reduction
  assert(io.in > -843314857.S, "input value is too small. must be greater then -pi/2 * 2^29")
  assert(io.in < 843314856.S, "input value is too large. must be smaller then pi/2 * 2^29")
  
  val atan_table_fp = VecInit(Seq(
    421657428.S(64.W),
    248918914.S(64.W),
    131521918.S(64.W),
    66762579.S(64.W),
    33510843.S(64.W),
    16771757.S(64.W),
    8387925.S(64.W),
    4194218.S(64.W),
    2097141.S(64.W),
    1048574.S(64.W),
    524287.S(64.W),
    262143.S(64.W),
    131071.S(64.W),
    65535.S(64.W),
    32767.S(64.W),
    16383.S(64.W)
  ))

  // State Registers
  val cos  = RegInit(326016437.S(64.W))    // Start with cos(0) = 1 in fixed point
  val sin  = RegInit(0.S(64.W))            // Start with sin(0) = 0 in fixed point
  val t    = RegInit(536870912.S(64.W))    // Start with t = 2^29
  val x    = RegInit(0.S(64.W))            // Register to hold the input angle
  val x1   = RegInit(0.S(64.W))            // 
  val step = RegInit(0.U(4.W))             // Register to track the iteration step

  val i = RegInit(0.U(8.W))
  val initialized = RegInit(false.B)
  val ready = RegInit(false.B)

  // Initialize the module
  when(io.resetRuntime) {
    i := 0.U
    initialized := false.B
    ready := false.B

    cos  := 326016437.S
    sin  := 0.S
    t    := 536870912.S
    x    := 0.S
    x1   := 0.S
    step := 0.U
  }

  val N = 29

  when(io.in =/= 0.S) {
    
    when(!initialized) {
      x := io.in
      initialized := true.B
    } .otherwise {

      when (i % 2.U === 0.U) {
        when (x >= 0.S) {
          x1 := cos - ((sin * t) >> N)
          sin := sin + ((cos * t) >> N)
          x := x - atan_table_fp(i / 2.U)
        } .otherwise {
          x1 := cos + ((sin * t) >> N)
          sin := sin - ((cos * t) >> N)
          x := x + atan_table_fp(i / 2.U)
        }
      } .otherwise {
        cos := x1
        t := t >> 1
      }
      i := i + 1.U
    }
  }

  when (i === 31.U) {
    ready := true.B
  }

  // Output the sine value
  io.done := ready
  io.out := sin
}
