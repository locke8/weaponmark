package mage
/**
  * Created by Paul on 9/24/2016.
  */
sealed trait DieResult {
  def roll : Die
  def value : Int
}
case class Success(roll : Die) extends DieResult {
  def value : Int = 1
}
case class Failure(roll : Die) extends DieResult {
  def value : Int = 0
}
case class Botch(roll : Die) extends DieResult {
  def value : Int = -1
}
case class SpecialSuccess(roll : Die, threshold: Int) extends DieResult {
  def calcBonusHits(accum: Int = 1) : Int = {
    val d = Die.roll
    if (d.isSpecial) calcBonusHits(accum + 1)
    else if (d.isSuccess(threshold)) accum + 1
    else if (d.isBotch) accum - 1 else accum
  }
  private val hits = calcBonusHits()
  def value : Int = hits
}

object DieResult {
  def rollToHit(difficulty : Int, specialty : Boolean = false) = {
    val r = Die.roll
    if (r.isSpecial && specialty) SpecialSuccess(r, difficulty)
    else if (r.isSuccess(difficulty)) Success(r)
    else if (r.isBotch) Botch(r) else Failure(r)
  }

  def rollDamage(difficulty : Int = 6) = {
    val r = Die.roll
    if (r.isSuccess(difficulty)) Success(r) else Failure(r)
  }
}