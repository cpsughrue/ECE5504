// See README.md for license details.

package sin

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage


class SinLookupTable extends Module {
  val io = IO(new Bundle {
    val index0 = Input(SInt(32.W))
    val index1 = Input(SInt(32.W))
    val out0 = Output(SInt(32.W)) 
    val out1 = Output(SInt(32.W)) 
  })

  val sinTable = VecInit(Seq(
     -134217728.S(32.W),
     -132387174.S(32.W),
     -126945442.S(32.W),
     -118040969.S(32.W),
     -105916647.S(32.W),
     -90903194.S(32.W),
     -73410140.S(32.W),
     -53914648.S(32.W),
     -32948505.S(32.W),
     -11083613.S(32.W),
     11083612.S(32.W),
     32948504.S(32.W),
     53914647.S(32.W),
     73410139.S(32.W),
     90903193.S(32.W),
     105916646.S(32.W), // 15
     118040968.S(32.W), // 16
     126945441.S(32.W),
     132387173.S(32.W),
     134217728.S(32.W)
  ))

  // Use the index to select a value from the lookup table
  io.out0 := sinTable(io.index0.asUInt)
  io.out1 := sinTable(io.index1.asUInt)
}

class XLookupTable extends Module {
  val io = IO(new Bundle {
    val index0 = Input(SInt(32.W))
    val index1 = Input(SInt(32.W))
    val out0 = Output(SInt(32.W)) 
    val out1 = Output(SInt(32.W)) 
  })

  val xTable = VecInit(Seq(

    -210828715.S(32.W),
    -188636218.S(32.W),
    -166443722.S(32.W),
    -144251226.S(32.W),
    -122058730.S(32.W),
    -99866234.S(32.W),
    -77673737.S(32.W),
    -55481241.S(32.W),
    -33288745.S(32.W),
    -11096249.S(32.W),
    11096248.S(32.W),
    33288744.S(32.W),
    55481240.S(32.W),
    77673736.S(32.W),
    99866233.S(32.W),
    122058729.S(32.W), // 15
    144251225.S(32.W), // 16
    166443721.S(32.W),
    188636217.S(32.W),
    210828714.S(32.W)
  ))

  // Use the index to select a value from the lookup table
  io.out0 := xTable(io.index0.asUInt)
  io.out1:= xTable(io.index1.asUInt)
}

class LinearInterpolate extends Module {
  val io = IO(new Bundle {
    val x0 = Input(SInt(32.W))
    val y0 = Input(SInt(32.W))
    val x1 = Input(SInt(32.W))
    val y1 = Input(SInt(32.W))
    val x  = Input(SInt(32.W))
    val out  = Output(SInt(32.W))
  })

  // ALL VALUES ARE Q5.27 FIXED POINT
  // out = y0 + (y1 - y0) * (x - x0) / (x1 - x0);

  val factor = 134217728.S

  val n1: SInt = io.y1 - io.y0
  val n2: SInt = io.x - io.x0
  val n3: SInt = (n1 * n2) / factor
  val n4: SInt = io.y0 + n3

  val d1: SInt = io.x1 - io.x0

  // Compute final result: y = y0 + result
  io.out := n4 / d1 * factor
}

class CalculateSinModule extends Module {
  val io = IO(new Bundle {
    val in = Input(SInt(32.W))
    val out = Output(SInt(32.W))
  })

  val sinLookupTable = Module(new SinLookupTable)
  val xLookupTable = Module(new XLookupTable)
  val linearInterpolate = Module(new LinearInterpolate)

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

  linearInterpolate.io.x0 := xOut0
  linearInterpolate.io.y0 := sinOut0
  linearInterpolate.io.x1 := xOut1
  linearInterpolate.io.y1 := sinOut1
  linearInterpolate.io.x := io.in

  printf("linear interpolation result: %d\n", linearInterpolate.io.out)

  io.out := sinOut0 + sinOut1
}

