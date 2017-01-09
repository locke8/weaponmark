package mage

import com.typesafe.scalalogging.LazyLogging

/**
  * Models the roll of a die using rollToHit() or rollDamage() and returns an algebraic type denoting the outcome
  * of the attempt. Each result includes a value (for accumulating results), as well as the actual 'Die' rolled.
  */
sealed trait DieResult extends LazyLogging with Product with Serializable {
  def die:        Die
  def value:      Int
  def roll:       Int     = die.value
  def difficulty: Int     = die.difficulty
  def isSuccess:  Boolean = die.isSuccess
  def name:       String  = this.getClass.getSimpleName
  def isBotch:    Boolean = false
  def isSpecial:  Boolean = false

  override def toString = s"$name($die, $value)"
}

final case class DieHit(die: Die) extends DieResult {
  def value: Int = 1
  require(!isBotch,               s"$this should return false from isBotch")
  require(isSuccess,              s"$this should return true from isSuccess")
  require(roll >= die.difficulty, s"$this should have a roll >= ${die.difficulty}")
}

final case class DieMiss(die: Die) extends DieResult {
  def value: Int = 0
  require(!isBotch,   s"$this should return false from isBotch")
  require(!isSuccess, s"$this should return false from isSuccess")
  require(!isSpecial, s"$this should return false from isSpecial")
}

final case class DieBotch(die: Die) extends DieResult {
  def value: Int = -1
  override def isBotch = true
  require(isBotch,    s"$this should return true from isBotch")
  require(!isSuccess, s"$this should return false from isSuccess")
  require(!isSpecial, s"$this should return false from isSpecial")
}

final case class DieSpecialHit(die: Die) extends DieResult {
  /** returns total number of hits from bonus rolls */
  private def rollBonusHits(bonusHits: Int) : Int = {
    val d = Die.roll(die.difficulty)
    logger.trace(s"         SpecialBonusRoll(${d.value})")

    if (d.isSpecial)      rollBonusHits(bonusHits + 1)
    else if (d.isSuccess) bonusHits + 1
    else if (d.isBotch)   bonusHits - 1 else bonusHits
  }
  override def isSpecial = true
  logger.trace("        Roll for Special Bonus Hits")
  private val hits = rollBonusHits(1)
  def value: Int = hits

  require(!isBotch,              s"$this should return false from isBotch")
  require(isSuccess,             s"$this should return true from isSuccess")
  require(isSpecial,             s"$this should return true from isSpecial")
  require(value >= 0,            s"$this should have a value >= 0")
  require(roll == Die.dieBase,   s"$this should have a roll = ${Die.dieBase}")
}

object DieResult extends LazyLogging {
  /**
    * Rolls one Die to hit and returns the result as a DieHit, DieMiss, DieBotch, or DieSpecialHit.
    *
    * @return a DieResult, one of the following: DieHit, DieMiss, DieBotch, or DieSpecialHit
    * @note add a note for pre or post conditions, or any other notable restrictions or expectations.
    * @see Core rules (WW4600), page xxx, ""
   */
  def rollToHit(difficulty: Int = 6, specialty: Boolean = false): DieResult = {
    val d = Die.roll(difficulty)
    val r = if (d.isSpecial && specialty) DieSpecialHit(d)
       else if (d.isSuccess)              DieHit(d)
       else if (d.isBotch)                DieBotch(d)
       else                               DieMiss(d)
    logger.trace("           {}", r)
    r
  }
  /**
    * Rolls one Die of damage and return sthe results as a DieHit or DieMiss.
    *
    * @return a DieHit or DieMiss containing the rolled die
    * @note There is no concept of botching when rolling damage a roll of 1 is returned as DieMiss(Die(1))
    * @see Core rules (WW4600), page 237, "Stage Three: Resolution"
   */
  def rollDamage(difficulty: Int = 6): DieResult = {
    val d = Die.roll(difficulty)
    val r = if (d.isSuccess) DieHit(d) else DieMiss(d)
    logger.trace("           {}", r)
    r
  }
}