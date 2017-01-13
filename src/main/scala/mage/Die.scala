package mage

/**
  * Models the result of rolling a single 10-sided die against a given 'difficulty'
  *
  * @param value the number (1-10) that was randomly rolled
  * @param difficulty the value needed to determine success or failure
  */
final case class Die(value: Int, difficulty: Int = 6) {
  def isBotch:   Boolean = value == 1
  def isSpecial: Boolean = value == Die.dieBase
  def isSuccess: Boolean = value >= difficulty
  require(value > 0 && (value <= Die.dieBase), "Die result must be between 1 and " + Die.dieBase)
  require(difficulty > 1 && (difficulty <= Die.dieBase), s"difficulty must be > 1 and <= ${Die.dieBase}")
}

/** Models the rolling of a single 10-sided die */
object Die {
  import scala.util.Random
  final val dieBase = 10
  private[this] val rnd = new Random

  /**
    * Rolls a 10-sided die against a supplied 'difficulty' and returns the results
    *
    * @param difficulty [optional] the value needed to determine success or failure, default value = 6
    * @return an instance of Die with the value that was rolled and the difficulty it was rolled against
    */
  def roll(difficulty: Int = 6) = Die(rnd.nextInt(dieBase) + 1, difficulty)
}