package weaponmark

import ammonite.ops._
import upickle.default.{read => unpickle}
import scala.math.abs
import mage.{Slow, UnitSpec}

class BenchmarkRegressionSpec extends UnitSpec {
  private def regressionTest(fp: Path) = {
    type Tup = (Benchmark, BenchmarkResults)
    val t = unpickle[Tup](read(fp))
    val b = t._1
    val r = t._2
    val r2 = Benchmark.newFrom(b).run()
    val deltaHpu = 0.011
    val deltaDpu = 0.01
    val deltaPb  = 0.04
    val deltaPm  = 0.16
    // the amount of hit-but-no-damage outcomes rises with soak dice efficacy
    val deltaPzd = if (b.opponent.soakDice > 0) 0.19 else 0.07

    info(s"${fp.segments.last} - ${b.weapon} ${b.opponent}")
    info(f"- hitsPerUse tolerance is ${abs(r2.hitsPerUse - r.hitsPerUse)}%1.3f should be <= +- $deltaHpu")
    (r.hitsPerUse === r2.hitsPerUse +- deltaHpu) shouldBe true
    info(f"- damPerUse  tolerance is ${abs(r2.damPerUse - r.damPerUse)}%1.3f should be <= +- $deltaDpu")
    (r.damPerUse === r2.damPerUse +- deltaDpu) shouldBe true
    info(f"- pctMisses  tolerance is ${abs(r2.pctMisses - r.pctMisses)}%1.3f should be <= +- $deltaPm")
    (r.pctMisses === r2.pctMisses +- deltaPm) shouldBe true
    info(f"- pctBotches tolerance is ${abs(r2.pctBotches - r.pctBotches)}%1.3f should be <= +- $deltaPb")
    (r.pctBotches === r2.pctBotches +- deltaPb) shouldBe true
    info(f"- pctZeroDam tolerance is ${abs(r2.pctZeroDam - r.pctZeroDam)}%1.3f should be <= +- $deltaPzd")
    (r.pctZeroDam === r2.pctZeroDam +- deltaPzd) shouldBe true
  }

  "A Quick Benchmark Regression test should" - {
    "match results (within tolerances) when re-run" in {
      val dir = pwd/'src/'test/'resources/'regression_data
      val files = ls! dir |? (_.ext == "bmr")
      regressionTest(files.head)
      info("5 regression tests run")
    }
  }
  // load saved benchmarks/results from file, re-run them and
  // compare results to the saved ones checking variance tolerances
  // should not be run at compile-time; run when building deployments
  "A Benchmark Regression Suite should" - {
    "for each saved benchmark file (*.bmr) in resources/regression_data" - {
      "match results (within tolerances) when re-run" taggedAs Slow in {
        val dir = pwd/'src/'test/'resources/'regression_data
        val files = ls! dir |? (_.ext == "bmr")
        files.foreach { path => regressionTest(path) }
        info(s"${files.size * 5} regression tests run")
      }
    }
  }
}
