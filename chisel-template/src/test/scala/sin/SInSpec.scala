// // See README.md for license details.

package sin

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

/**
  * This is a trivial example of how to run this Specification
  * From within sbt use:
  * {{{
  * testOnly sin.SinSpec
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly sin.SinSpec'
  * }}}
  * Testing from mill:
  * {{{
  * mill %NAME%.test.testOnly sin.SinSpec
  * }}}
  */

class SinSpec extends AnyFreeSpec with Matchers {

  "CalculateSinModule should calculate proper value" in {

    simulate(new LookupSinModule) { sinModule =>
      sinModule.io.in.poke(536870912.S)
      sinModule.clock.step() // Step the clock to propagate signals
      sinModule.io.out.expect(451761052.S)
    }
  }
}
