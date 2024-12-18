package freechips.rocketchip.tile

import chisel3._
import chisel3.util._

object LinearInterpolate {

  // all values are Q5.27 fixed point 
  def apply(x0: SInt = Wire(SInt(32.W)), 
            y0: SInt = Wire(SInt(32.W)), 
            x1: SInt = Wire(SInt(32.W)), 
            y1: SInt = Wire(SInt(32.W)), 
            x:  SInt = Wire(SInt(32.W))
  ): SInt = {

    val deltaY = y1 - y0
    val deltaX = x - x0

    val numerator = Wire(SInt(64.W))
    numerator := deltaY * deltaX

    val denominator = x1 - x0

    val fraction = numerator / denominator

    y0 + fraction
  }
}
