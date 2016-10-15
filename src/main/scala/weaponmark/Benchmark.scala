package weaponmark

import mage.{Success, Weapon, DieResult}
import org.scalactic.TimesOnInt._

case class Benchmark(
  weapon:     Weapon,
  verbose:    Boolean = false,
  soakDice:   Int = 0,
  iterations: Int = 1000000) {

  private val results = BenchmarkResults(this)

  // use/fire/swing the weapon once, return true if botch occurs
  private def use(diceMod: Int = 0): Boolean = {
    var (hits, damage) = weapon.use(diceMod)
    if (hits > 0) {
      soakDice times {
        DieResult.rollDamage() match {
          case Success(r) => damage -= 1
          case _ =>
        }
      }
      results.totHits += hits
      results.totDamage += damage
      if (damage == 0) results.tot0Damage += 1
    }
    else if (hits < 0) results.totBotches += 1
    else results.totMisses += 1
    hits < 0
  }

  // iterate over one turn worth of weapon use
  private def iterate() = {
    var botch = false
    val uses = weapon.actionsPerTurn - (if (weapon.actionsInSplit > 0) 1 else 0)
    uses times {
      if (!botch) botch = use()
    }
    if (weapon.actionsInSplit > 0) {
      if (!botch) botch = use(weapon.splitDmod1)
      if (!botch) botch = use(weapon.splitDmod2)

      if (!botch && (weapon.actionsInSplit > 2)) botch = use(weapon.splitDmod3)
    }
  }

  def run() : BenchmarkResults = {
    iterations times { iterate() }
    results
  }
}