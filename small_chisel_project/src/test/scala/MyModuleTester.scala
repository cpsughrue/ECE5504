import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class MyModuleTester extends AnyFlatSpec with ChiselScalatestTester {
  "MyModule" should "pass basic tests" in {
    test(new MyModule) { dut =>
      dut.io.in.poke(42.U)
      dut.io.out.expect(42.U)
    }
  }
}
