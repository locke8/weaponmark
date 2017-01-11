package weaponmark

import scala.math.abs

import ammonite.ops.{Path, ls, pwd, read}
import upickle.default.{read => unpickle}

/**
  * Determines optimum tolerance values for running benchmark regression tests and print results.
  * @note Each iteration will take 15-20 seconds or so depending on cpu.
  */
object ToleranceTuner extends App {
  private[this] val iterations = args(0).toInt
  private[this] var deltaPzd = 0.0d
  private[this] var deltaHpu = 0.0d
  private[this] var deltaDpu = 0.0d
  private[this] var deltaPm  = 0.0d
  private[this] var deltaPb  = 0.0d

  def processFile(p: Path): Unit = {
    type Tup = (Benchmark, BenchmarkResults)
    val (b, r) = unpickle[Tup](read(p))
    print(".")
    val r2 = Benchmark.newFrom(b).run()

    val dPzd = abs(r2.pctZeroDam - r.pctZeroDam)
    val dHpu = abs(r2.hitsPerUse - r.hitsPerUse)
    val dDpu = abs(r2.damPerUse - r.damPerUse)
    val dPm  = abs(r2.pctMisses - r.pctMisses)
    val dPb  = abs(r2.pctBotches - r.pctBotches)

    if (dPzd > deltaPzd) deltaPzd = dPzd
    if (dHpu > deltaHpu) deltaHpu = dHpu
    if (dDpu > deltaDpu) deltaDpu = dDpu
    if (dPm > deltaPm) deltaPm = dPm
    if (dPb > deltaPb) deltaPb = dPb
  }

  val dir = pwd/'src/'test/'resources/'regression_data
  val files = ls! dir |? (_.ext == "bmr")
  println(s"Running the tuner for $iterations iterations:")
  for (pass <- 1 to iterations) {
    print(s"\n$pass:")
    files.foreach { path => processFile(path) }
  }
  println(s"\nTuning complete. Results:")
  println(s"deltaPzd = $deltaPzd")
  println(s"deltaHpu = $deltaHpu")
  println(s"deltaDpu = $deltaDpu")
  println(s"deltaPm  = $deltaPm")
  println(s"deltaPb  = $deltaPb")
}