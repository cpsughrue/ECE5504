package sin

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage

object LinearInterpolate {

  // all values are Q5.27 fixed point 
  def apply(x0: SInt, y0: SInt, x1: SInt, y1: SInt, x: SInt): SInt = {

    val deltaY = y1 - y0
    val deltaX = x - x0
    val numerator = (deltaY * deltaX) >> 27

    val denominator = x1 - x0

    val fraction = numerator / denominator

    y0 + fraction
  }
}