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
