package weaponmark

import com.diogonunes.jcdp.color.ColoredPrinter
import com.diogonunes.jcdp.color.api.Ansi.{Attribute, BColor, FColor}

sealed case class BenchmarkResults(
  benchmark:      Benchmark,
  var tot0Damage: Int = 0,
  var totBotches: Int = 0,
  var totMisses:  Int = 0,
  var totHits:    Int = 0,
  var totDamage:  Int = 0) {

  private val weapon = benchmark.weapon
  private val verbose = benchmark.verbose
  private val soakDice = benchmark.soakDice
  private val iterations = benchmark.iterations
  private def totUses = weapon.usesPerTurn * iterations
  private def hitsPerUse = totHits.toDouble / totUses
  private def hitsPerTurn = totHits.toDouble / iterations
  private def damPerUse = totDamage.toDouble / totUses
  private def damPerTurn = totDamage.toDouble / iterations
  private def pctMisses = (totMisses.toDouble / totUses) * 100
  private def pctBotches = (totBotches.toDouble / totUses) * 100
  private def pctZeroDam = (tot0Damage.toDouble / totUses) * 100
  private def pctIneffective = pctMisses + pctBotches + pctZeroDam

  private final val cp = new ColoredPrinter.Builder(1, false)
    .foreground(FColor.WHITE).background(BColor.BLACK)
    .build()

  private def printTitle() = {
    cp.clear()
    cp.print(weapon.name + (if (weapon.specialty) " (specialized)" else ""), Attribute.CLEAR, FColor.CYAN, BColor.BLACK)
    cp.clear()
    cp.println(
      ": " + weapon.usesPerTurn + " uses/turn" +
      (if (weapon.actionsInSplit > 0) f" including a split-action-${weapon.actionsInSplit}" else "") +
      (if (soakDice > 0) s" plus $soakDice soak dice"  else "") +
      f" for $iterations%,8d turns:")
  }

  private def printHeader() = {
    cp.clear()
    cp.setForegroundColor(FColor.GREEN)
    cp.println("   Hits/Use   Hits/Turn   Dam/Use   Dam/Turn" +
      (if (verbose) "     %Miss + %Botch + %0Damage = %Ineffective" else ""))
  }

  private def printDetail() = {
    cp.clear()
    cp.print(f" $hitsPerUse%10.2f $hitsPerTurn%11.2f $damPerUse%9.2f", Attribute.CLEAR, FColor.YELLOW, BColor.BLACK)
    cp.print(f"$damPerTurn%11.2f", Attribute.BOLD, FColor.CYAN, BColor.BLACK)
    if (verbose) {
      cp.print(f"   $pctMisses%6.2f%% $pctBotches%7.2f%%$pctZeroDam%10.2f%%",
        Attribute.CLEAR, FColor.YELLOW, BColor.BLACK)
      cp.print(f" $pctIneffective%13.2f%%",Attribute.CLEAR, FColor.CYAN, BColor.BLACK)
    }
    cp.clear()
    cp.println("")
  }

  def output() = {
    printTitle()
    printHeader()
    printDetail()
  }
}