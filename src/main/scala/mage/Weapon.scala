package mage
/**
  * Created by Paul on 9/24/2016.
  */
class Weapon(val name : String,
             hitDice: Int,
             dmgDice: Int,
             hitDiff: Int = 6,
             dmgDiff: Int = 6,
             val splitDmod1: Int = -2,
             val splitDmod2: Int = -3,
             val splitDmod3: Int = -4,
             val specialty: Boolean = false,
             val actionsInSplit: Int = 0,
             val actionsPerTurn: Int = 1) {

  override def toString = name + " (" + hitDice + "/" + dmgDice + "/" + hitDiff + ")"

  // returns # of hit successes or zero
  private def hits(dice: Int, threshold : Int = 6, accum: Int = 0) : Int =
    if (dice > 0) {
      val r = DieResult.rollToHit(threshold, specialty)
      hits(dice - 1, threshold, accum + r.value)
    } else accum

  // returns number of damage successes
  private def damage(dice: Int, accum: Int = 0, threshold : Int = dmgDiff) : Int =
    if (dice == 0) accum
    else {
      val r = DieResult.rollDamage(threshold)
      damage(dice - 1, accum + r.value, threshold)
    }

  // returns tuple where first element is # of hits and second is # of
  // damage successes. Note that damage will be 0 if hits < 1
  def use(diceMod : Int = 0, threshold : Int = hitDiff) : (Int, Int) = {
    val h = hits(hitDice + diceMod, threshold)
    val d = if (h > 0) damage(dmgDice + h - 1) else 0
    (h, d)
  }

  def usesPerTurn = actionsPerTurn + actionsInSplit - (if (actionsInSplit > 0) 1 else 0)
}