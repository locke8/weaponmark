package mage

import com.typesafe.scalalogging.LazyLogging

/**
  * Models an opponent or subject of the attack.
  * For now this consists of opponents who can soak damage when hit.
  * Use rollToSoak() to determine amount of damage soaked.
  * Use Opponent.defenseless to get an opponent that cannot soak damage.
  * Armor and DamageType could also be modelled here.
  *
  * @param soakDice Int - the number of soaking dice to roll, default = 0 or no soak
  * @param soakDifficulty Int - a number between 2 and 10 representing the minimum roll needed to succeed/soak a hit.
  *                       default is 6 per core rules.
  */
final case class Opponent(soakDice: Int = 0, soakDifficulty: Int = 6) extends LazyLogging {
  /** returns number of successful soaks rolled; zero if no successes */
  def rollToSoak() : Int = {
    def soak(dice: Int, successes: Int = 0): Int =
      if (dice == 0)
        if (successes > 0) successes else 0
      else {
        val r = DieResult.rollDamage(soakDifficulty)
        soak(dice - 1, successes + r.value)
      }
    if (soakDice > 0) {
      logger.trace(s"            Roll for Soak:  dice=$soakDice")
      soak(soakDice)
    } else 0
  }

  // throw IllegalArgumentException for any invalid constructor parameters
  require(soakDice >= 0 && (soakDice <= Opponent.maxSoak), s"Soak Dice($soakDice) must be between 0 and ${Opponent.maxSoak}")
  require(soakDifficulty > 1 && (soakDifficulty <= Die.dieBase), s"Soak Difficulty($soakDifficulty) must be between 2 and ${Die.dieBase}")
}

object Opponent {
  val maxSoak = 20
  val baseSoakDifficulty = 6
  val defenseless = Opponent()
}