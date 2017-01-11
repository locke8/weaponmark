/**
  * To enable debug logging change: <root level="ERROR"> to: <root level="TRACE"> in resources/logback.xml
  * Note: make sure to set this back to "ERROR" before attempting to run regression tests (BenchmarkRegressionSpec)
  */
package weaponmark

import com.diogonunes.jcdp.color.ColoredPrinter
import com.diogonunes.jcdp.color.api.Ansi.{Attribute, BColor, FColor}
import com.typesafe.scalalogging.LazyLogging
import org.rogach.scallop._
import scala.language.reflectiveCalls
import mage.{Opponent, Weapon, Die}


/**
  * Benchmarks the use of a weapon in the game Mage:The Ascension revised edition and produce statistics
  * showing its effectiveness.
  */
object WeaponMark extends App with LazyLogging {

  /** create a ColoredPrinter to use for printing all output */
  private val cp = new ColoredPrinter.Builder(1, false)
    .foreground(FColor.WHITE).background(BColor.BLACK)
    .build()

  private def printTitle() = {
    cp.clear()
    cp.print(s"\n${BuildInfo.name} ", Attribute.CLEAR, FColor.CYAN, BColor.BLACK)
    cp.print(s"${BuildInfo.version}-BETA", Attribute.CLEAR, FColor.YELLOW, BColor.BLACK)
    cp.print(s" for Mage: The Ascension (WW4600 - Revised Edition)\n", Attribute.CLEAR, FColor.WHITE, BColor.BLACK)
  }

  /** open default browser on readme.md page on github */
  def showAboutInfo(): Unit = {
    val url = "https://github.com/locke8/weaponmark"
    Runtime.getRuntime.exec( "rundll32 url.dll,FileProtocolHandler " + url)
  }

  /** configure the command line arguments */
  private def configure(options: Seq[String]) = {
    overrideColorOutput.value = Option(false)
    new ScallopConf(options) {
      helpWidth(110)
      banner(s"""How much damage does your weapon dish out?
               |
               | Usage:
               |  weaponmark [OPTIONS] weaponName hitDice damageDice [hitDifficulty] [damageDifficulty]
               |
               | Options:""".stripMargin)
      val about        = opt[Boolean]("about", noshort = true, descr = "explains purpose and usage of weaponmark (opens default web browser)")
      val specialty    = opt[Boolean]("specialty", short = 'l', descr = s"any roll of ${Die.dieBase} earns a bonus roll, if that roll is 1 the bonus is negated")//, a bonus roll of 1 negates the bonus")
      val interactive  = opt[Boolean]("interactive", short = 'i', descr = "you will be queried for all needed input")
      val examples     = opt[Boolean]("examples", short = 'e', descr = "show examples of usage with explanations")
      val version      = opt[Boolean]("version", noshort = true, descr = "show weaponmark version information")
      val help         = opt[Boolean]("help", short = 'h', descr = "show this message")
      val actions      = opt[Int]("actions", short = 'a', default = Some(1),
        descr = s"actions per turn. min=default=1, max=${Weapon.maxActionsPerTurn}",
        validate       = (d: Int) => d > 0 && (d <= Weapon.maxActionsPerTurn))
      val multiActions = opt[Int]("multiActions", short = 'm', default = Some(0),
        descr = s"actions in a Multiple-Action (p.215), 2 to ${Weapon.maxMultiActions} or 0 (default) for none",
        validate = (d: Int) => d == 0 || (d > 1 && (d <= Weapon.maxMultiActions)))
      val dMod1        = opt[Int]("dMod1", short = 'x',
        descr = s"dice pool penalty for 1st multi-action. default=multiActions, max=${Weapon.maxMultiPenalty}",
        validate = (d: Int) => d <= Weapon.maxMultiPenalty && (d >= 0))
      val dMod2        = opt[Int]("dMod2", short = 'y',
        descr = s"dice pool penalty for 2nd multi-action. default=multiActions+1, max=${Weapon.maxMultiPenalty}",
        validate = (d: Int) => d <= Weapon.maxMultiPenalty  && (d >= 0))
      val dMod3        = opt[Int]("dMod3", short = 'z',
        descr = s"dice pool penalty for 3rd multi-action. default=multiActions+2, max=${Weapon.maxMultiPenalty}",
        validate = (d: Int) => d <= Weapon.maxMultiPenalty && (d >= 0))
      val kiloTurns    = opt[Int]("kiloTurns", short = 't', default = Some(Benchmark.minKiloTurns),
        descr = s"turns to benchmark, expressed in thousands. default=${Benchmark.minKiloTurns}, max=${Benchmark.maxKiloTurns}",
        validate = (d: Int) => d >= Benchmark.minKiloTurns && (d <= Benchmark.maxKiloTurns))
      val soakDice     = opt[Int]("soakDice", short = 's', default = Some(0),
        descr = s"dice to roll that can soak damage. default=0, max=${Opponent.maxSoak}",
        validate = (d: Int) => d >= 0 && (d <= Opponent.maxSoak))
      val soakDiff     = opt[Int]("soakDifficulty", short = 'd', default = Some(Opponent.baseSoakDifficulty),
        descr = s"difficulty of soaking damage. default=${Opponent.baseSoakDifficulty}, max=${Die.dieBase}",
        validate = (d: Int) => d >= 0 && (d <= Die.dieBase))
      val weaponName   = trailArg[String]("weaponName", required = true,
        descr = "e.g. Kick, Rifle, \"Long Sword\" - use quotes if there's a space in the name")
      val hitDice      = trailArg[Int]("hitDice", required = true,
        descr = s"dice to roll when attacking. min=1, max=${Weapon.maxHitDice}",
        validate = (d: Int) => d > 0 && (d <= Weapon.maxHitDice))
      val damageDice   = trailArg[Int]("damageDice", required = true,
        descr = s"dice to roll when determining damage. min=1, max=${Weapon.maxDamageDice}",
        validate = (d: Int) => d > 0 && (d <= Weapon.maxDamageDice))
      val hitDifficulty = trailArg[Int]("hitDifficulty", required = false, default = Some(Weapon.baseHitDifficulty),
        descr=s"difficulty of scoring a hit. default=${Weapon.baseHitDifficulty}, max=${Die.dieBase}",
        validate = (d: Int) => d > 0 && (d <= Die.dieBase))
      val damageDifficulty = trailArg[Int]("damageDifficulty", required = false, default = Some(Weapon.baseDamageDifficulty),
        descr=s"difficulty of scoring damage. default=${Weapon.baseDamageDifficulty}, max=${Die.dieBase}",
        validate = (d: Int) => d > 0 && (d <= Die.dieBase))
      verify()
    }
  }

  /** constructs the Benchmark then runs and prints the results */
  private def run(arguments: Seq[String]) : Unit = {
    val opts = configure(arguments)
    /** returns the default dice pool penalty for each action in a multi-action */
    def dicePoolPenalty(multiAction: Int) : Int = opts.multiActions() + multiAction - 1
    val turns = if (logger.underlying.isDebugEnabled) 100 else opts.kiloTurns() * 1000
    val opponent = Opponent(opts.soakDice(), opts.soakDiff())
    val weapon = Weapon(
      opts.weaponName(),
      opts.hitDice(),
      opts.damageDice(),
      opts.hitDifficulty(),
      opts.damageDifficulty(),
      opts.actions(),
      opts.multiActions(),
      opts.dMod1.getOrElse(dicePoolPenalty(1)),
      opts.dMod2.getOrElse(dicePoolPenalty(2)),
      opts.dMod3.getOrElse(dicePoolPenalty(3)),
      opts.specialty())

    val b = Benchmark(weapon, opponent, turns)
    b.run()
    b.printResults()
  }

  // if no params passed then start interview
  val a = if (args.isEmpty) Array("-i") else args
  // remove leading hyphens and lower case the first argument
  val arg0 = a(0).substring(a(0).lastIndexOf('-') + 1).toLowerCase

  printTitle()
  arg0 match {
    case "about"             => showAboutInfo()
    case "e" | "examples"    => Example(cp)
    case "i" | "interactive" => run(Interview(cp).results)
    case "version"           => // already handled by call to printTitle() just above
    case _                   => run(a)
  }
}