package mage

import com.typesafe.scalalogging.LazyLogging

/**
  * Models a weapon as needed for benchmarking purposes.
  * It can be used to mount an attack() and determine how many usesPerTurn() may be attempted; note that a botch
  * will abort any actions/attacks remaining in that round.
  * The attack() method is instrumented for logging at the level of TRACE, use when debugging.
  * Require expressions validate constructor parameters, remove from production compile with flag -Xdisable-assertions.
  *
  * @param name any name you want, spaces are ok
  * @param hitDice the # of dice to roll when attacking (to see if a hit occurs)
  * @param dmgDice the # of dice to roll when calculating damage (when a hit occurs)
  * @param hitDiff the difficulty of scoring a hit, range 2 to 10, default=6
  * @param dmgDiff the difficulty of scoring damage, range 2 to 10, default=6
  * @param actionsPerTurn used (with multiActions) to compute attempted uses per turn, range 1 to 9, default=1
  * @param multiActions used (with actionsPerTurn) to compute attempted uses per turn, range 0 and 2-3, default=0
  * @param dicePenalty1 dice penalty for 1st multi-action. default=multiActions, range 0 to 9.
  *                     Subtracted from hitDice when attacking
  * @param dicePenalty2 dice penalty for 1st multi-action. default=multiActions, range 0 to 9.
  *                     Subtracted from hitDice when attacking
  * @param dicePenalty3 dice penalty for 1st multi-action. default=multiActions, range 0 to 9.
  *                     Subtracted from hitDice when attacking
  * @param specialty true if 10 earns bonus rolls, false otherwise. As described on page 117 of the core rules book.
  *                  If you are specialized in the use of your weapon, every 10 you roll to hit yields a bonus roll.
  *                  Additional rolls of 10 extend this effect. Rolling a 1 on the bonus roll cancels out the 10.
  */
final case class Weapon(
    name:           String,
    hitDice:        Int,
    dmgDice:        Int,
    hitDiff:        Int = 6,
    dmgDiff:        Int = 6,
    actionsPerTurn: Int = 1,
    multiActions:   Int = 0,
    dicePenalty1:   Int = 0,
    dicePenalty2:   Int = 0,
    dicePenalty3:   Int = 0,
    specialty:      Boolean = false )
  extends LazyLogging {

  override def toString = s"$name ($hitDice/$dmgDice/$hitDiff/$dmgDiff/$actionsPerTurn/$multiActions/$dicePenalty1/$dicePenalty2/$dicePenalty3/$specialty)"

  /** returns an AttackResult (which includes the hit and damage successes) */
  def attack(diceMod: Int = 0, opponent: Opponent) : AttackResult = {
    val dice = hitDice - diceMod
    logger.trace(s"             Roll for Attack:  penalty dice=$diceMod remaining dice=$dice difficulty=$hitDiff")
    val result = AttackResult.attack(dice, this, opponent)
    logger.trace(s"             $result")
    result
  }

  /**
    * @return Int - the actual # of times the weapon will attempt to be used each turn
    * @note a botch will cancel any remaining uses for the current turn
    */
  def usesPerTurn: Int = actionsPerTurn + (if (multiActions > 1) multiActions - 1 else 0)

  require(name.nonEmpty)
  require(actionsPerTurn >  0 && (actionsPerTurn <= Weapon.maxActionsPerTurn))
  require(multiActions   >= 0 && (multiActions   <= Weapon.maxMultiActions))
  require(hitDice        >  0 && (hitDice        <= Weapon.maxHitDice))
  require(dmgDice        >  0 && (dmgDice        <= Weapon.maxDamageDice))
  require(hitDiff        >  1 && (hitDiff        <= Die.dieBase))
  require(dmgDiff        >  1 && (dmgDiff        <= Die.dieBase))
  require(dicePenalty1   >= 0 && (dicePenalty1   <= Weapon.maxMultiPenalty))
  require(dicePenalty2   >= 0 && (dicePenalty1   <= Weapon.maxMultiPenalty))
  require(dicePenalty3   >= 0 && (dicePenalty1   <= Weapon.maxMultiPenalty))
}

object Weapon {
  val maxHitDice           = 20
  val maxDamageDice        = 30
  val maxActionsPerTurn    =  9
  val maxMultiActions      =  3
  val maxMultiPenalty      =  9
  val baseHitDifficulty    =  6
  val baseDamageDifficulty =  6
}