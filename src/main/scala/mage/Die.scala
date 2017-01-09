package mage

final case class Die(value: Int, difficulty: Int = 6) {
  def isBotch:   Boolean = value == 1
  def isSpecial: Boolean = value == Die.dieBase
  def isSuccess: Boolean = value >= difficulty
  require(value > 0 && (value <= Die.dieBase), "Die result must be between 1 and " + Die.dieBase)
  require(difficulty > 1 && (difficulty <= Die.dieBase), s"difficulty must be > 1 and <= ${Die.dieBase}")
}

object Die {
  import scala.util.Random
  final val dieBase = 10
  private[this] val rnd = new Random

  /** returns a Die with a random value between 1 and dieBase */
  def roll(difficulty: Int = 6) = Die(rnd.nextInt(dieBase) + 1, difficulty)
}