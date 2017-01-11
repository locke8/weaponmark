package weaponmark

import com.diogonunes.jcdp.color.ColoredPrinter
import com.diogonunes.jcdp.color.api.Ansi.{Attribute, BColor, FColor}

final case class BenchmarkResultsPrinter(b: Benchmark, r: BenchmarkResults) {

  private val weapon = b.weapon
  private val soakDice = b.opponent.soakDice
  private val turns = b.turns
  private val cp = new ColoredPrinter.Builder(1, false)
    .foreground(FColor.WHITE).background(BColor.BLACK)
    .build()

  private def printTitle() = {
    cp.clear()
    cp.println("")
    cp.print(weapon.name + (if (weapon.specialty) " (specialized)" else ""), Attribute.CLEAR, FColor.CYAN, BColor.BLACK)
    cp.clear()
    cp.println(
      ": " + weapon.usesPerTurn + " attempted uses/turn" +
        (if (weapon.multiActions > 0) f" including a multi-action(${weapon.multiActions})" else "") +
        (if (soakDice > 0) s" plus $soakDice soak dice"  else "") +
        f" for $turns%,d turns:")
  }

  private def printHeader() = {
    cp.clear()
    cp.setForegroundColor(FColor.GREEN)
    cp.println("  Hits/Use  Hits/Turn  Dam/Use  Dam/Turn" +
      "   %Miss + %Botch + %0Damage = %Ineffective = Chance")
  }

  private def printDetail() = {
    cp.clear()
    cp.print(f"${r.hitsPerUse}%10.2f ${r.hitsPerTurn}%10.2f ${r.damPerUse}%8.2f", Attribute.CLEAR, FColor.YELLOW, BColor.BLACK)
    cp.print(f"${r.damPerTurn}%10.2f", Attribute.BOLD, FColor.CYAN, BColor.BLACK)
    cp.print(f"${r.pctMisses}%7.2f%% ${r.pctBotches}%7.2f%%${r.pctZeroDam}%10.2f%%", Attribute.CLEAR, FColor.YELLOW, BColor.BLACK)
    cp.print(f"${r.pctIneffective}%14.2f%%",Attribute.CLEAR, FColor.CYAN, BColor.BLACK)
    cp.print(f"   1 in ${100.0 / (r.pctIneffective + .05) }%2.1f", Attribute.CLEAR, FColor.CYAN, BColor.BLACK)
    cp.clear()
    cp.println("")
  }

  def print(): Unit = {
    printTitle()
    printHeader()
    printDetail()
  }
}