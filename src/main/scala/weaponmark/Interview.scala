/**
  * Consider using TextIO to provide input masking, value constraints, selection from a list and possibly custom error messages
  * Consider using JLine2 for same
  * https://dzone.com/articles/interactive-console-applications-in-java?edition=244484
  * https://github.com/beryx/text-io
  */
package weaponmark

import mage.{Weapon, Opponent}
import com.diogonunes.jcdp.color.ColoredPrinter
import com.diogonunes.jcdp.color.api.Ansi.{Attribute, BColor, FColor}

/** Solicits the user for all command-line input needed to run a benchmark. */
case class Interview(cp: ColoredPrinter) {
  private final val input = scala.io.StdIn

  private def readString(prompt: String, default: String) : String = {
    cp.print(prompt, Attribute.DARK, FColor.GREEN, BColor.BLACK)
    cp.clear()
    val i = input.readLine()
    if (i.isEmpty) {
      cp.print("  " + default + "\n", Attribute.LIGHT, FColor.BLACK, BColor.BLACK)
      cp.clear()
      default
    } else i
  }

  private def readInt(prompt: String, default: Int) : Int = {
    cp.print(prompt, Attribute.DARK, FColor.GREEN, BColor.BLACK)
    cp.clear()
    var v = 0
    try { v = input.readInt() }
    catch { case _: NumberFormatException =>
      cp.print("  " + default + "\n", Attribute.LIGHT, FColor.BLACK, BColor.BLACK)
      cp.clear()
      v = default }
    v
  }
  
  def readBoolean(prompt: String, default: Boolean = false): Boolean = {
    cp.print(prompt, Attribute.DARK, FColor.GREEN, BColor.BLACK)
    cp.clear()
    val s = input.readLine()
    if (s.isEmpty) {
      cp.print("  " + (if (default) "Yes\n" else "No\n"), Attribute.LIGHT, FColor.BLACK, BColor.BLACK)
      cp.clear()
      default
    } else
      s.toLowerCase() match {
        case "true" | "t" => true
        case "yes"  | "y" => true
        case _            => false
      }
  }
  
  private def printCmdLine(args: Seq[String]) = {
    val s = args.toString().substring(5).dropRight(1).replaceAll(",", "")
    cp.println(s"\n= weaponmark $s\n")
  }

  /** Asks the user for all necessary input and returns the results as a Seq[String] */
  def run() : Seq[String] = {
    cp.println("")
    val name    = readString("Weapon Name: ", "WeaponX")
    val hitDice = readInt("Hit Dice: ", 1)
    val dmgDice = readInt("Damage Dice: ", 1)
    val hitDiff = readInt(s"Hit Difficulty, default = ${Weapon.baseHitDifficulty}: ", Weapon.baseHitDifficulty)
    val dmgDiff = readInt(s"Damage Difficulty, default = ${Weapon.baseDamageDifficulty}: ", Weapon.baseDamageDifficulty)

    val spec = readBoolean("Specialty bonus y/n (p.117) (default = n): ")

    val actions  = readInt("Actions per turn (1 to x, default = 1): ", 1)
    val soakDice = readInt("Enemy soak dice (0 to x, default = 0): ", 0)
    val p0 = s"Enemy soak dice difficulty (2 to 10, default = ${Opponent.baseSoakDifficulty}): "
    val soakDiff = if (soakDice > 0) readInt(p0, Opponent.baseSoakDifficulty) else Opponent.baseSoakDifficulty
    val mAction  = readInt("Multi-Actions (p.215) (0, 2, or 3, default=0=no multi-action): ", 0)

    // if multi-action, ask about custom hit-dice-pool penalties
    val p1 = "  Hit dice pool penalty for 1st use (default = -multiActions): "
    val dMod1 = if (mAction > 0) readString(p1, "") else ""
    val p2 = "  Hit dice pool penalty for 2nd use (default = -multiActions+1): "
    val dMod2 = if (mAction > 1) readString(p2, "") else ""
    val p3 = "  Hit dice pool penalty for 3rd use (default = -multiActions+2): "
    val dMod3 = if (mAction > 2) readString(p3, "") else ""

    val args =
      (if (!spec)           Seq.empty else Seq("-l")) ++
      (if (actions < 2)     Seq.empty else Seq("-a" + actions.toString)) ++
      (if (mAction < 1)     Seq.empty else Seq("-m" + mAction)) ++
      (if (dMod1.isEmpty)   Seq.empty else Seq("-x" + dMod1)) ++
      (if (dMod2.isEmpty)   Seq.empty else Seq("-y" + dMod2)) ++
      (if (dMod3.isEmpty)   Seq.empty else Seq("-z" + dMod3)) ++
      (if (soakDice < 1)    Seq.empty else Seq("-s" + soakDice)) ++
      (if (soakDice < 1)    Seq.empty else Seq("-d" + soakDiff)) ++
      (if (name.contains(" "))             Seq(f"""\"$name\"""") else Seq(name)) ++
                                           Seq(hitDice.toString, dmgDice.toString) ++
      (if (hitDiff == Weapon.baseHitDifficulty)
                            Seq.empty else Seq(hitDiff.toString)) ++
      (if (dmgDiff == Weapon.baseDamageDifficulty)
                            Seq.empty else Seq(dmgDiff.toString))

    printCmdLine(args)
    args
  }
}