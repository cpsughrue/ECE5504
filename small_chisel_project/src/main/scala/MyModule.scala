import chisel3._

class MyModule extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  // Example logic: pass-through
  io.out := io.in
}
