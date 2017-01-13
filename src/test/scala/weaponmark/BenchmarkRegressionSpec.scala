package weaponmark

import ammonite.ops._
import upickle.default.{read => unpickle}
import scala.math.abs
import mage.{Slow, UnitSpec}

/**
  * Re-runs saved benchmarks and compares new results with old results to ascertain that no errors have been introduced
  * as the result of changes. Results will vary slightly from run to run due to the nature of the benchmark (random dice)
  * so test equality is tolerance based. These tests should be run after making changes to the benchmark and must be
  * run before creating a distribution build.
  *
  * Saved benchmarks (and their results) can be found in test/resources/regression_data. Run RegressionTestFileGenerator
  * if constructor signature of Benchmark, BenchmarkResults, Weapon, or Opponent classes are changed as this will
  * invalidate the format of the regression files and break the tests.
  * RegressionTestToleranceTuner can be run to determine tolerances (or pretty darn close). The tolerance values are
  * hard-coded in the class below.
  */
class BenchmarkRegressionSpec extends UnitSpec {
  private def regressionTest(fp: Path) = {
    type Tup = (Benchmark, BenchmarkResults)
    val (b, r) = unpickle[Tup](read(fp))
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
        files.size shouldBe 25
      }
    }
  }
}
