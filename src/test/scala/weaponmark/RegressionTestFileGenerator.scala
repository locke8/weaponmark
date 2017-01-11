package weaponmark

import ammonite.ops.{home, write}
import mage.{Opponent, Weapon}
import upickle.{default => pickle}

/**
  * create benchmark regression test files to use as base line in regression testing - BenchmarkRegressionSpec
  * after running this, you might want to run ToleranceTuner(100) - and then go to lunch while it runs.
  */
object RegressionTestFileGenerator extends App {

  private var count = 0

  def createRegressionFile(n: String, w: Weapon, o: Opponent = Opponent.defenseless): Unit = {
    val b = Benchmark(w, o, turns = 500000)
    val r = b.run()
    // temporary location, when satisfied move files to weaponmark/src/test/resources/regression_data
    val fPath = home / 'weaponmark / n
    type Tup = (Benchmark, BenchmarkResults)
    val t: Tup = (b, r)
    write.over(fPath, pickle.write(t))
    print(".")
    count += 1
  }

  println("Running the regression-test file generator:")

  // 1 action no multi-actions
  createRegressionFile("punch1.bmr",
    Weapon("Punch", 9, 4, specialty = true))
  // 1 action 2 multi-actions
  createRegressionFile("punch12.bmr",
    Weapon("Punch", 9, 4, 6, 6, 1, 2, specialty = true))
  // 1 action 3 multi-actions
  createRegressionFile("punch13.bmr",
    Weapon("Punch", 9, 4, 6, 6, 1, 3, specialty = true))

  // 2 actions no multi-actions
  createRegressionFile("punch2.bmr",
    Weapon("Punch", 9, 4, 6, 6, 2, specialty = true))
  createRegressionFile("armored_punch2.bmr",
    Weapon("Armored Punch", 9, 5, 6, 6, 2, specialty = true))
  createRegressionFile("kick2.bmr",
    Weapon("Kick", 9, 4, 6, 6, 2))
  createRegressionFile("hvy_kick2.bmr",
    Weapon("Heavy Kick", 9, 6, 7, 6, 2))
  createRegressionFile("pistol_dw2.bmr",
    Weapon("Pistol (DW Ambidex)", 9, 5, 6, 6, 2))
  createRegressionFile("rifle2.bmr",
    Weapon("PN90 Rifle", 9, 7, 6, 6, 2, specialty = true))

  // 2 actions second is a multi-action(2)
  createRegressionFile("punch22.bmr",
    Weapon("Punch", 9, 4, 6, 6, 2, 2, specialty = true))
  createRegressionFile("armored_punch22.bmr",
    Weapon("Armored Punch", 9, 5, 6, 6, 2, 2, specialty = true))
  createRegressionFile("kick22.bmr",
    Weapon("Kick", 9, 4, 6, 6, 2, 2))
  createRegressionFile("hvy_kick22.bmr",
    Weapon("Heavy Kick", 9, 6, 7, 6, 2, 2))
  createRegressionFile("pistol_dw22.bmr",
    Weapon("Pistol (DW Ambidex)", 9, 5, 6, 6, 2, 2, 1, 2))
  createRegressionFile("rifle22.bmr",
    Weapon("PN90 Rifle", 9, 7, 6, 6, 2, 2, specialty = true))

  // 2 actions second is a multi-action(3)
  createRegressionFile("punch23.bmr",
    Weapon("Punch", 9, 4, 6, 6, 2, 3, specialty = true))
  createRegressionFile("armored_punch23.bmr",
    Weapon("Armored Punch", 9, 5, 6, 6, 2, 3, specialty = true))
  createRegressionFile("kick23.bmr",
    Weapon("Kick", 9, 4, 6, 6, 2, 3))
  createRegressionFile("hvy_kick23.bmr",
    Weapon("Heavy Kick", 9, 6, 7, 6, 2, 3))
  createRegressionFile("pistol_dw23.bmr",
    Weapon("Pistol (DW Ambidex)", 9, 5, 6, 6, 2, 3, 2, 3, 4))
  createRegressionFile("rifle23.bmr",
    Weapon("PN90 Rifle", 9, 7, 6, 6, 2, 3, specialty = true))

  // against opponent with 4 Soak Dice
  createRegressionFile("punch13s4.bmr",
    Weapon("Punch", 9, 4, 6, 6, 1, 3, specialty = true), Opponent(4))
  // a powerful attack against opponent with 9 Soak Dice - stress test results from so much soak
  createRegressionFile("powerful2s8.bmr",
    Weapon("Powerful Attack", 10, 9, 6, 6, 2), Opponent(9))
  // rifle does lethal damage soak difficulty set to 8
  createRegressionFile("rifle22s4.bmr",
    Weapon("PN90 Rifle", 9, 7, 6, 6, 2, 2), Opponent(4, 8))
  createRegressionFile("rifle23s4.bmr",
    Weapon("PN90 Rifle", 9, 7, 6, 6, 2, 3, specialty = true), Opponent(4, 8))

  println(s"\nfile generator completed successfully: $count test files created.")
}