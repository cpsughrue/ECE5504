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
      sinModule.io.in.poke(421657428.S)
      sinModule.clock.step() // Step the clock to propagate signals
      sinModule.io.out.expect(379624726.S)
    }
    
    simulate(new ChebyshevSinModule) { sinModule =>
      sinModule.reset.poke(true.B) // Explicit reset
      sinModule.clock.step()
      sinModule.reset.poke(false.B)
      sinModule.io.in.poke(421657428.S)
      sinModule.clock.step() // Step the clock to propagate signals
      sinModule.io.out.expect(379625044.S)
    }
    
    simulate(new CordicSinModule) { sinModule =>
      sinModule.io.resetRuntime.poke(true.B)
      sinModule.clock.step()
      sinModule.io.resetRuntime.poke(false.B)

      
      sinModule.io.in.poke(421657428.S)
      sinModule.clock.step(33) // 1 step to initialize x, 32 steps to update then sync
      sinModule.io.out.expect(379630958.S)
      sinModule.io.done.expect(true.B)
    }
  }
}
