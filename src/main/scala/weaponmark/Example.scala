package weaponmark

import com.diogonunes.jcdp.color.ColoredPrinter
import com.diogonunes.jcdp.color.api.Ansi.{Attribute, BColor, FColor}

case class Example(cp: ColoredPrinter) {
  private def print() = {
    val cmdColor = FColor.YELLOW
    val descColor = FColor.CYAN

    cp.setForegroundColor(FColor.YELLOW)
    cp.println("Benchmark weapon performance in Mage: The Ascension (WW4600 - Revised Edition)")
    cp.println("Works for martial arts, brawling, melee, and firearm weapons.")
    cp.clear()
    cp.println("\nweaponmark [OPTIONS] weaponName hitDice damageDice [hitDifficulty] [damageDifficulty]\n")
    cp.println("   Examples:\n", Attribute.CLEAR, cmdColor, BColor.BLACK)
    cp.println("""
   1) weaponmark -l "Long Sword" 8 7""",Attribute.CLEAR, cmdColor, BColor.BLACK)
    cp.println("""

      Benchmark a "Long Sword" (quotes needed for space) with
      8 hit dice, 7 damage dice, and default 6 hit difficulty
      and 6 damage difficulty.
      Wielded as a specialty (-l) = bonus roll to hit on 10""", Attribute.DARK, descColor, BColor.BLACK)

    cp.println("""

   2) weaponmark -lv Heavy Kick 8 6 7""", Attribute.CLEAR, cmdColor, BColor.BLACK)
    cp.println("""

      Benchmark a Heavy Kick with 8 hit dice, 7 hit difficulty,
      6 damage dice & damage difficulty. Wielded as a specialty
      (-l) and running verbose (-v) to show miss-related stats.""", Attribute.CLEAR, descColor, BColor.BLACK)
    cp.println("""

   3) weaponmark -la2 -m2 -x1 -y2 "Pistols (dual-wield)" 8 5""", Attribute.CLEAR, cmdColor, BColor.BLACK)
    cp.println("""

      Dual wield pistols, specialty use (-l = bonus roll on 10).
      Two actions a turn (-a2) including a 2-use multi-action (-m2)
      for a total of 3 attempts per turn. Multi-action hit dice mod
      penalties are 1 and 2 (-x1 and -y2) instead of default
      values (2,3). The Pistols have 8 hit dice and do 5 dice of
      damage.""", Attribute.CLEAR, descColor, BColor.BLACK)
    cp.println("""

   4) weaponmark -i""", Attribute.CLEAR, cmdColor, BColor.BLACK)
    cp.println("""

      Interactive mode. You will be queried for all needed input.
               """, Attribute.CLEAR, descColor, BColor.BLACK)
    cp.clear()
  }

  print()
}
