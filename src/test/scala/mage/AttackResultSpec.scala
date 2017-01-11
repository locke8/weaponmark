package mage

import org.scalactic.TimesOnInt._
import org.scalatest.Assertion

class AttackResultSpec extends UnitSpec {
  private val success    = AttackSucceeded(1,0)
  private val fail       = AttackFailed(0)
  private val botch      = AttackBotched(-1)

  sealed trait AttackValidator {
    val weapon = Weapon("Rifle", 8, 7, 6, 6, 2, 2, specialty = true)
    val opponent = Opponent(3)

    protected def validate(result: AttackResult): Assertion = {
      result match {
        case AttackSucceeded(_, _) =>
          result.hits should be > 0
          result.damage should be > -1
          result.isBotch shouldBe false
        case AttackFailed(_, _) =>
          result.hits shouldBe 0
          result.damage shouldBe 0
          result.isBotch shouldBe false
        case AttackBotched(_, _) =>
          result.hits should be < 0
          result.damage shouldBe 0
          result.isBotch shouldBe true
      }
    }
  }

  "An AttackResult" - {
    "when successful should" - {
      "have one or more hits"    in { success.hits    should be > 0 }
      "have zero or more damage" in { success.damage  should be > -1 }
      "not be a botch"           in { success.isBotch shouldBe false }
    }
    "when failed should" - {
      "have zero hits"           in { fail.hits    shouldBe 0 }
      "have zero damage"         in { fail.damage  shouldBe 0 }
      "not be a botch"           in { fail.isBotch shouldBe false }
  }
    "when a botch should" - {
      "have less than zero hits" in { botch.hits    should be < 0 }
      "have zero damage"         in { botch.damage  shouldBe 0 }
      "be a botch"               in { botch.isBotch shouldBe true }
    }
    f"when used to attack $iterations%,d times should" - {
      "be a valid child of AttackResult every time" in new AttackValidator() {
        iterations times {
          validate(AttackResult.attack(Die.roll().value, weapon, opponent))
        }
      }
    }
  }
}