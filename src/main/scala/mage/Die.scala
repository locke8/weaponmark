package mage
/**
  * Created by Paul on 9/24/2016.
  */
case class Die(result : Int) {
  require(result > 0 && (result <= Die.dieBase), "Die roll must be between 1 and " + Die.dieBase)

  def isSpecial : Boolean = result == Die.dieBase
  def isBotch : Boolean = result == 1
  def isSuccess(threshold : Int = 6) : Boolean = result >= threshold
}

object Die {
  import scala.util.Random
  final val dieBase = 10
  private val rnd = new Random

  def roll = Die(rnd.nextInt(dieBase) + 1)
}