package weaponmark

import com.diogonunes.jcdp.color.ColoredPrinter
import com.diogonunes.jcdp.color.api.Ansi.{Attribute, BColor, FColor}
import org.rogach.scallop._
import mage.Weapon

import scala.language.reflectiveCalls

object WeaponMark  extends App {
  private final val input = scala.io.StdIn

  val desc = "for Mage: The Ascension (WW4600 Edition), by Paul L. Bryan"
  private def id() : String = s"${BuildInfo.name} ${BuildInfo.version} $desc"

  private def printTitle() = {
    cp.clear()
    cp.println(s"\n${BuildInfo.name} ", Attribute.CLEAR, FColor.CYAN, BColor.BLACK)
    cp.println(BuildInfo.version, Attribute.CLEAR, FColor.YELLOW, BColor.BLACK)
    cp.println(s" $desc\n\n", Attribute.CLEAR, FColor.WHITE, BColor.BLACK)
  }

  private final val cp = new ColoredPrinter.Builder(1, false)
    .foreground(FColor.WHITE).background(BColor.BLACK)
    .build()

  private def configure(options: Seq[String]) = {
    overrideColorOutput.value = Option(false)
    new ScallopConf(options) {
      helpWidth(100)
      version(id())
      banner(s"""How much damage does your weapon dish out?
               |
               | Usage:
               |  weaponmark [OPTIONS] weaponName hitDice damageDice [hitDifficulty] [damageDifficulty]
               |
               | Options:""".stripMargin)
      val specialty = opt[Boolean]("specialty", short = 'l', descr = "roll of 10 earns a bonus roll")
      val verbose = opt[Boolean]("verbose", short = 'v', descr = "Include %Ineffective stats like %Miss")
      val interactive = opt[Boolean]("interactive", short = 'i', descr = "you will be queried for all options")
      val examples = opt[Boolean]("examples", short = 'e', descr = "Print examples of usage with explanations")
      val version = opt[Boolean]("version", noshort = true, descr = "Print version information")
      val help = opt[Boolean]("help", short = 'h', descr = "Show this message")
      val actions = opt[Int]("actions", short = 'a', default = Some(1),
        descr = "actions per turn, default=1, max=9",
        validate = (d: Int) => d > 0 && (d < 10))
      val actionsInSplit = opt[Int]("actionsInSplit", short = 's', default = Some(0),
        descr = "# of actions in a split-action (2 or 3), default=0=no split action",
        validate = (d: Int) => d == 0 || (d > 1 && (d < 4)))
      val dMod1 = opt[Int]("dMod1", short = 'x', default = Some(-2),
        descr = "penalty dice-mod for split action 1, default=-2 max=-9",
        validate = (d: Int) => d > -10 && (d <= 0))
      val dMod2 = opt[Int]("dMod2", short = 'y', default = Some(-3),
        descr = "penalty dice-mod for split action 2, default=-3 max=-9",
        validate = (d: Int) => d > -10 && (d <= 0))
      val dMod3 = opt[Int]("dMod3", short = 'z', default = Some(-4),
        descr = "penalty dice-mod for split action 3, default=-4, max=-9",
        validate = (d: Int) => d > -10 && (d <= 0))
      val megaTurns = opt[Int]("megaTurns", short = 't', default = Some(1),
        descr = "millions of iterations of benchmark, default=1, max=50",
        validate = (d: Int) => d > 0 && (d < 51))
      val soakDice = opt[Int]("soakDice", short = 'k', default = Some(0),
        descr = "# of dice to roll that can soak damage, default=0, max=9",
        validate = (d: Int) => d >= 0 && (d < 10))
      val weaponName = trailArg[String]("weaponName", required = true,
        descr = "e.g. Kick, Rifle, \"Long Sword\"")
      val hitDice = trailArg[Int]("hitDice", required = true,
        descr = "# of dice to roll when attacking, max=19",
        validate = (d: Int) => d > 0 && (d < 20)) // (0<))
      val damageDice = trailArg[Int]("damageDice", required = true,
        descr = "# of dice to roll when determining damage, max=19",
        validate = (d: Int) => d > 0 && (d < 20))
      val hitDifficulty = trailArg[Int]("hitDifficulty", required = false, default = Some(6),
        descr="Difficulty of scoring a hit, default=6",
        validate = (d: Int) => d > 0 && (d < 11))
      val damageDifficulty = trailArg[Int]("damageDifficulty", required = false, default = Some(6),
        descr="Difficulty of scoring damage, default=6",
        validate = (d: Int) => d > 0 && (d < 11))
      verify()
    }
  }

  private def printExamples() = {
    val cmdColor = FColor.YELLOW
    val descColor = FColor.CYAN

    printTitle()
    cp.setForegroundColor(FColor.YELLOW)
    cp.println("Benchmark weapon performance in Mage: The Ascension (WW4600 Edition)")
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

   2) weaponmark -lv Kick 8 7 7""", Attribute.CLEAR, cmdColor, BColor.BLACK)
    cp.println("""

      Benchmark a Kick with 8 hit dice, 7 hit difficulty, 7 damage
      dice, and 6 (default) damage difficulty. Wielded as a specialty
      (-l) and running verbose (-v) to show miss-related stats.""", Attribute.CLEAR, descColor, BColor.BLACK)
    cp.println("""

   3) weaponmark -la2 -s2 -x -1 -y -2 "Pistols (dual-wield)" 8 5""", Attribute.CLEAR, cmdColor, BColor.BLACK)
    cp.println("""

      Dual wield pistols, specialty use (-l = bonus roll on 10).
      Two actions a turn (-a2) including a 2-use split action (-s2)
      for a total of 3 uses per turn. Split action to hit dice mod
      penalties are -1 and -2 (-x -1 and -y -2) instead of default
      values (-2,-3). The Pistols have 8 hit dice and do 5 dice of
      damage.""", Attribute.CLEAR, descColor, BColor.BLACK)
    cp.println("""

   4) weaponmark -i""", Attribute.CLEAR, cmdColor, BColor.BLACK)
    cp.println("""

      Interactive mode. You will be queried for all needed input.
      """, Attribute.CLEAR, descColor, BColor.BLACK)
    cp.clear()
  }

  private def runAkira() : Unit = {
    printTitle()
    cp.println("===  Akira's Weapons  ===\n", Attribute.BOLD, FColor.GREEN, BColor.BLACK)
    cp.clear()
    // punch
    val f = new Weapon("Punch", 8, 4, 6, 6, -2, -3, -4, false, 3, 2)
    Benchmark(f, verbose = true).run().output()
    // kick
    val k2 = new Weapon("Kick", 8, 5, 6, 6, -2, -3, -4, true, 3, 2)
    Benchmark(k2, verbose = true).run().output()
    // pistol
    val p = new Weapon("Pistol", 8, 5, 6, 6, -2, -3, -4, false, 3, 2)
    Benchmark(p, verbose = true).run().output()
    // pistol dual-wield
    val p2 = new Weapon("Pistols (dual-wield)", 8, 5, 6, 6, -1, -2, -3, false, 3, 2)
    Benchmark(p2, verbose = true).run().output()
    // rifle
    val r = new Weapon("PN90 Rifle", 8, 7, 6, 6, -2, -3, -4, true, 3, 2)
    Benchmark(r, verbose = true).run().output()
  }

  private def run(targs: Seq[String]) : Unit = {
    val opts = configure(targs)
    val w = new mage.Weapon(
      opts.weaponName(),
      opts.hitDice(),
      opts.damageDice(),
      opts.hitDifficulty(),
      opts.damageDifficulty(),
      opts.dMod1(),
      opts.dMod2(),
      opts.dMod3(),
      opts.specialty(),
      opts.actionsInSplit(),
      opts.actions())

    Benchmark(w,opts.verbose(), opts.soakDice(), opts.megaTurns() * 1000000).run().output()
  }

  private def readString(prompt: String, default: String) : String = {
    cp.print(prompt, Attribute.DARK, FColor.GREEN, BColor.BLACK)
    cp.clear()
    val i = input.readLine()
    if (i.isEmpty) default else i
  }

  private def readInt(prompt: String, default: Int) : Int = {
    cp.print(prompt, Attribute.DARK, FColor.GREEN, BColor.BLACK)
    cp.clear()
    var v = 0
    try { v = input.readInt() }
    catch { case n: NumberFormatException => v = default }
    v
  }

  private def printCmdLine(args: Seq[String]) = {
    val s = args.toString().substring(5).dropRight(1).replaceAll(",", "")
    cp.println(s"\n= weaponmark $s\n")
  }

  private def runInteractive() = {
    printTitle()

    val name = readString("Weapon Name: ", "WeaponX")

    val hitDice = readString("Hit Dice: ", "1")

    val dmgDice = readString("Damage Dice: ", "1")

    val hitDiff = readString("Hit Difficulty, default = 6: ", "")

    val dmgDiff = readString("Damage Difficulty, default = 6: ", "")

    cp.print("Specialty bonus (page 117), (y/n, default = n): ", Attribute.DARK, FColor.GREEN, BColor.BLACK)
    cp.clear()
    val spec = input.readBoolean

    val actions = readInt("Actions per turn (1 to x, default = 1): ", 1)

    val soakDice = readInt("Enemy soak dice (0 to x, default = 0): ", 0)

    val sAction = readInt("Uses in a Split Action (0=no split action, 2, or 3): ", 0)

    // if split action, ask for dice mod penalties
    val p1 = "  Dice mod penalty to hit for 1st use, default = -2: "
    val dMod1 = if (sAction > 0) readString(p1, "") else ""
    val p2 = "  Dice mod penalty to hit for 2nd use, default = -3: "
    val dMod2 = if (sAction > 1) readString(p2, "") else ""
    val p3 = "  Dice mod penalty to hit for 3rd use, default = -4: "
    val dMod3 = if (sAction > 2) readString(p3, "") else ""

    val arg1 = "-v" +
      (if (spec) "l" else "") +
      (if (actions > 1) "a" + actions.toString else "")

    val args = Seq(arg1) ++
      (if (sAction < 1)   Seq.empty else Seq("-s" + sAction)) ++
      (if (dMod1.isEmpty) Seq.empty else Seq("-x" + dMod1)) ++
      (if (dMod2.isEmpty) Seq.empty else Seq("-y" + dMod2)) ++
      (if (dMod3.isEmpty) Seq.empty else Seq("-z" + dMod3)) ++
      (if (soakDice < 1)  Seq.empty else Seq("-k" + soakDice)) ++
      (if (name.contains(" ")) Seq(f"""\"$name\"""") else Seq(name)) ++
      Seq(hitDice, dmgDice) ++
      (if (hitDiff.isEmpty) Seq.empty else Seq(hitDiff)) ++
      (if (dmgDiff.isEmpty) Seq.empty else Seq(dmgDiff))

    printCmdLine(args)
    run(args)
  }

  // if no params passed, show help screen
  val a = if (args.isEmpty) Array("-h") else args
  // remove leading hyphens and lower case value from arg(0)
  val arg0 = a(0).substring(a(0).lastIndexOf('-') + 1).toLowerCase

  arg0 match {
    case "akira" => runAkira()
    case "e" => printExamples()
    case "examples" => printExamples()
    case "i" => runInteractive()
    case "interactive" => runInteractive()
    case "h" => run(a)
    case "help" => run(a)
    case "version" => printTitle()
    case default => printTitle() ; run(a)
  }
}