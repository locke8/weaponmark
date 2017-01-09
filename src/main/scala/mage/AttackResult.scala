package mage

import com.typesafe.scalalogging.LazyLogging

/**
  * models the result of an attack using the attack() method and returns an algebraic type denoting the outcome
  * as well as the # of hits and damage that occurred
  */
sealed trait AttackResult extends Product with Serializable {
  val hits: Int
  val damage: Int
  def botched: Boolean = false
}

final case class AttackSucceeded(hits: Int, damage: Int) extends AttackResult {
  require(hits > 0,    s"hits($hits) rolled must be > zero if an attack succeeds")
  require(damage >= 0, s"damage($damage) rolled must be >= zero on attack success")
  require(!botched,    s"botched ($botched) should return false")
}

final case class AttackFailed(hits: Int, damage: Int = 0) extends AttackResult {
  require(hits == 0,   s"hits($hits) rolled must be zero if an attack fails")
  require(damage == 0, s"damage rolled ($damage) must be zero if an attack fails")
  require(!botched,    s"botched ($botched) should return false")
}

final case class AttackBotched(hits: Int, damage: Int = 0) extends AttackResult {
  override def botched = true
  require(hits <= 0,   s"hits($hits) rolled must be <= zero if a botch occurs")
  require(damage == 0, s"damage rolled ($damage) must be zero if a botch occurs")
  require(botched,     s"botched ($botched) should return true")
}

object AttackResult extends LazyLogging {

  /**
    * returns the result of a single attack attempt (including any effective hits and damage)
    * as an AttackSucceeded, AttackFailed or AttackBotched.
    * rolls 'dice' number of dice, one at a time, and tallies the results to determine if the attack was a success,
    * failure, or botch. The total number of hits and damage, if any, are returned within the AttackResult.
    *
    * @param dice the total # of attack dice available for this attack.
    *             calculated as weapon.hitDice adjusted by any multi-action dice pool penalties.
    * @param weapon the weapon to be used in this attack
    * @param opponent the target of this attack (who may have soak dice)
    * @param hits accumulator for total effective hits rolled, don't provide, allow default value to be used.
    * @param botches accumulator for total botches rolled during the attack, don't provide, allow default value.
    * @param rawHits accumulator for total number of raw successes (before considering any rolls of 1, i.e. "botches"),
    *                don't provide a value, allow the default value to be used.
    * @see See core rules p
    */
   /* something below causing build error with ScalaDoc
   * @example {{{
   * val w = Weapon("PN90 Rifle", 9, 7, 6, 6, 2, 2, specialty = true)
   * val result = AttackResult.attack(9, w, Opponent.defenseless)
   * // taken from Benchmark.useWeapon()
   * result match {
   *   case AttackBotched(_, _) =>
   *     results.totBotches += 1
   *   case AttackFailed(_, _) =>
   *     results.totMisses += 1
   *   case AttackSucceeded(hits, damage) =>
   *     results.totHits += hits
   *     results.totDamage += damage
   *     if (damage < 1) results.tot0Damage += 1
   * }
   * }}}
   * @return the results of rolling 'dice' # of attack dice, tallying the results to determine if a hit,
   *         miss, or botch occurred and return the total number of effective hits and damage (if any)
   *         wrapped in an AttackSucceeded, AttackFailed or AttackBotched
   */
  def attack(
      dice:     Int,
      weapon:   Weapon,
      opponent: Opponent,
      hits:     Int = 0,
      botches:  Int = 0,
      rawHits:  Int = 0): AttackResult =
    if (dice == 0) {
      logger.trace(s"        hits=$hits rawSuccesses=$rawHits botches=$botches")
      require(hits <= rawHits, s"hits($hits) must be less than or equal to rawSuccesses($rawHits)")
      if (rawHits < 1 && (botches > 0))
        AttackBotched(hits)
      else if (hits > 0)
        AttackSucceeded(hits, damage(hits - 1, weapon, opponent))
      else
        AttackFailed(0)
    } else {
      val r = DieResult.rollToHit(weapon.hitDiff, weapon.specialty)
      attack(dice - 1, weapon, opponent,
        hits + r.value,
        botches + (if (r.isBotch) 1 else 0),
        rawHits + (if (r.value > 0) r.value else 0))
    }

  /**
    * returns effective damage successes or zero if no damage rolled. Effective damage is that
    * which is left after considering opponent defenses (soak dice).
    *
    * @return Int - the total effective damage scored
    */
  private def damage(bonusDice: Int, weapon: Weapon, opponent: Opponent) : Int = {
    // returns number of damage successes without considering opponent's defenses (soak dice)
    def rawDamage(dice: Int, difficulty: Int, successes: Int = 0) : Int =
      if (dice == 0)
        successes
      else
        rawDamage(dice - 1, difficulty, successes + DieResult.rollDamage(difficulty).value)

    logger.trace(s"       Roll for Damage:  dice=${weapon.dmgDice} plus bonusDice=$bonusDice (hits - 1)")
    val dice = weapon.dmgDice + bonusDice
    val dam = rawDamage(dice, weapon.dmgDiff) - opponent.rollToSoak()
    require(dam <= dice, s"Damage rolled ($dam) cannot exceed number of damage dice ($dice)!")
    if (dam > 0) dam else 0
  }
}