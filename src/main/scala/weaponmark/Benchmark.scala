package weaponmark

import com.typesafe.scalalogging.LazyLogging
import org.scalactic.TimesOnInt._
import mage.{AttackBotched, AttackFailed, AttackSucceeded, Weapon, Opponent}

final case class Benchmark(weapon: Weapon, opponent: Opponent, turns: Int = 200000) extends LazyLogging {

  require(turns <= Benchmark.maxKiloTurns * 1000)
  // minimum turns not enforced to support debugging runs where only 100 or so may be needed or desired

  private val results = BenchmarkResults(weapon.usesPerTurn, turns)
  private val border = "================================================================================"

  private def logHeader() = {
    logger.debug(border)
    logger.debug(s"= Begin Benchmark for $weapon, $turns turns, ${weapon.usesPerTurn} attempted uses per turn")
    logger.debug(s"=   actions=${weapon.actionsPerTurn}  multi-actions=${weapon.multiActions}")
    if (weapon.multiActions > 1) logger.debug(s"=   dMod1=${weapon.dicePenalty1}  dMod2=${weapon.dicePenalty2}" +
      (if (weapon.multiActions > 2) s"  dMod3=${weapon.dicePenalty3}" else ""))
  }

  private def logFooter() = {
    logger.debug("= ")
    logger.debug(s"= End Benchmark for $weapon, $turns turns, ${weapon.usesPerTurn} attempted uses per turn")
  }

  /**
    * use/fire/swing the weapon once collecting results as statistics
    * @return ''true'' on hit or miss (weapon was used) OR ''false'' if botch occurs
    */
  private def useWeapon(diceMod: Int = 0) : Boolean = {
    val result = weapon.attack(diceMod, opponent)

    // tally results
    result match {
      case AttackBotched(_, _) =>
        results.incBotches()
      case AttackFailed(_, _) =>
        results.incMisses()
      case AttackSucceeded(hits, damage) =>
        results.incHitsBy(hits)
        results.incDamageBy(damage)
        if (damage < 1) results.incZeroDamage()
    }
    !result.isBotch
  }

  /** performs all weapon actions/usages for a single turn */
  private def iterateOneTurn() = {
    /** performs any specified regular actions, stops on botch, returns true on success or false if botch occurred */
    def doNormalActions(times: Int, noBotch: Boolean = true) : Boolean =
      if (times > 0 && noBotch)
        doNormalActions(times - 1, useWeapon())
      else noBotch
    /** performs any specified multi-actions, stops on botch, returns true on success or false if botch occurred */
    def doMultiActions() =
      if (weapon.multiActions > 1) {
        logger.trace(s"   Begin Multi-Action:  ${weapon.multiActions} separate actions")
        useWeapon(weapon.dicePenalty1) &&
          useWeapon(weapon.dicePenalty2) &&
          ((weapon.multiActions < 3) || useWeapon(weapon.dicePenalty3))
      }

    val normalActions = weapon.actionsPerTurn - (if (weapon.multiActions > 1) 1 else 0)
    logger.trace(s"  Begin New Turn:  normal_actions=$normalActions + multi_actions=${weapon.multiActions}")

    // if a botch occurs during normal action(s), skip any multi-actions for this turn
    if (doNormalActions(normalActions))
      doMultiActions()
  }

/**
  * executes the benchmark for ''turns'' turns, logging activity and collecting stats/results as it goes
  * @return BenchmarkResults chronicling the run of this benchmark
 */
  def run(): BenchmarkResults = {
    logHeader()
    turns times { iterateOneTurn() }
    logFooter()
    results
  }

  def printResults(): Unit = BenchmarkResultsPrinter(this, results).print()
}

object Benchmark {
  val minKiloTurns = 200
  val maxKiloTurns = 1000

  /** returns a new (un-run) Benchmark using 'b' to supply the constructor parameters */
  def newFrom(b: Benchmark): Benchmark = Benchmark(b.weapon, b.opponent, b.turns)
}