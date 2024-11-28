// ~/rocket/chipyard-clean/generators/chipyard/src/main/scala/config/ECE5504RocketConfigs.scala

package chipyard

import chisel3._

import freechips.rocketchip.config.{Config, Parameters}
import freechips.rocketchip.rocket._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.tile._

// Do not modify
class ECE5504AbstractRocketConfig extends Config(
  new chipyard.config.WithNPerfCounters ++
  new chipyard.config.WithBroadcastManager ++ // remove L2
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++ // Rocket-chip core
  new chipyard.config.WithSystemBusFrequencyAsDefault ++
  new chipyard.config.WithSystemBusFrequency(500.0) ++
  new chipyard.config.WithMemoryBusFrequency(500.0) ++
  new chipyard.config.WithPeripheryBusFrequency(500.0) ++
  new chipyard.config.AbstractConfig)

/**********************************************************************
 * Cache Blocks
 **********************************************************************/

class ECE5504RocketConfig extends Config(
  new WithL1ICacheSets(64) ++
  new WithL1ICacheWays(1) ++
  new WithL1DCacheSets(8) ++
  new WithL1DCacheWays(8) ++
  new ECE5504AbstractRocketConfig ++
  new WithCacheBlockBytes(64)) // Size of each line in a set (i.e 64 bytes), DO NOT MODIFY..!!!

class ECE5504RocketL2Config extends Config(
  new WithInclusiveCache(nBanks = 1, nWays = 8, capacityKB = 64) ++
  new ECE5504RocketConfig)


/**********************************************************************
 * Cache prefetching
 **********************************************************************/

class ECE5504RocketNoPrefetchConfig extends Config(
  new freechips.rocketchip.subsystem.WithNonblockingL1(nMSHRs = 4) ++ // use non-blocking L1D
  new freechips.rocketchip.subsystem.WithNBanks(2) ++ // increase number of broadcast hub trackers
  new ECE5504AbstractRocketConfig)

// Evaluation CONFIG with prefetching enabled
class ECE5504RocketPrefetchConfig extends Config(
  new WithL1Prefetcher ++               // enable L1 prefetcher
  new ECE5504RocketNoPrefetchConfig)

class WithL1Prefetcher extends Config((site, here, up) => {
  case BuildL1Prefetcher => Some((p: Parameters) => Module(new ExampleL1Prefetcher()(p)))
})


// class WithCustomSinAccelerator extends Config((site, here, up) => {
//   case BuildRoCC => up(BuildRoCC) ++ Seq((p: Parameters) => {
//       val customSin = LazyModule(new CustomSinAccelerator(OpcodeSet.custom0)(p))
//       customSin
//     })
// })

// class CustomSinRocketConfig extends Config(
//   new WithCustomSinAccelerator ++
//   new RocketConfig // Using Chipyard's RocketConfig as base
// )

class CustomSinRocketConfig extends Config(
  new WithRoccExample ++
  new RocketConfig // Using Chipyard's RocketConfig as base
)
