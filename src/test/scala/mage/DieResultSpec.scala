package mage

import org.scalactic.TimesOnInt._

class DieResultSpec extends UnitSpec {
  private val specialResult = DieSpecialHit(Die(Die.dieBase, difficulty))
  private val hitResult     = DieHit(Die(difficulty, difficulty))
  private val missResult    = DieMiss(Die(difficulty - 1, difficulty))
  private val botchResult   = DieBotch(Die(1, difficulty))

  private def dieHitAsserts(result: DieHit) = {
    result.isBotch   shouldBe false
    result.isSuccess shouldBe true
    result.isSpecial shouldBe false
    result.value     shouldBe 1
    result.roll      should be >= difficulty
  }

  private def dieMissAsserts(result: DieMiss) = {
    result.isBotch   shouldBe false
    result.isSuccess shouldBe false
    result.isSpecial shouldBe false
    result.value     shouldBe 0
    result.roll      should be < difficulty
  }

  private def validate(result: DieResult) = {
    result match {
      case DieSpecialHit(_) =>
        result.isBotch   shouldBe false
        result.isSuccess shouldBe true
        result.isSpecial shouldBe true
        result.value     should be >= 0
        result.roll      shouldBe Die.dieBase
      case DieBotch(_) =>
        result.isBotch   shouldBe true
        result.isSuccess shouldBe false
        result.isSpecial shouldBe false
        result.value     shouldBe -1
        result.roll      shouldBe 1
      case DieHit(_) =>
        dieHitAsserts(result.asInstanceOf[DieHit])
      case DieMiss(_) =>
        dieMissAsserts(result.asInstanceOf[DieMiss])
    }
  }
  "A DieResult" - {
    "when a successful hit should" - {
      "not be special"    in { hitResult.isSpecial shouldBe false }
      "be successful"     in { hitResult.isSuccess shouldBe true }
      "not be a botch"    in { hitResult.isBotch   shouldBe false }
      "have a value = 1"  in { hitResult.value     shouldBe 1 }
      "have a roll > 5"   in { hitResult.roll      should be >= difficulty }
    }
    "when a special hit should" - {
      "be special"        in { specialResult.isSpecial shouldBe true }
      "be successful"     in { specialResult.isSuccess shouldBe true }
      "not be a botch"    in { specialResult.isBotch   shouldBe false }
      "have a value >= 0" in { specialResult.value     should be >= 0 }
      "have a roll = 10"  in { specialResult.roll      shouldBe 10 }
    }
    "when a miss should" - {
      "not be special"    in { missResult.isSpecial shouldBe false }
      "not be successful" in { missResult.isSuccess shouldBe false }
      "not be a botch"    in { missResult.isBotch   shouldBe false }
      "have a value = 0"  in { missResult.value     shouldBe 0 }
      "have a roll < 6"   in { missResult.roll      should be < difficulty }
    }
    "when a botch should" - {
      "not be special"    in { botchResult.isSpecial shouldBe false }
      "not be successful" in { botchResult.isSuccess shouldBe false }
      "be a botch"        in { botchResult.isBotch   shouldBe true }
      "have a value = -1" in { botchResult.value     shouldBe -1 }
      "have a roll = 1"   in { botchResult.roll      shouldBe 1 }
    }
    f"when used to roll hits $iterations%,d times via rollToHit($difficulty, true) should" - {
      "be a valid child of DieResult every time" in {
        iterations times { validate(DieResult.rollToHit(difficulty, specialty = true)) }
        }
    }
    f"when used to roll damage $iterations%,d times via rollDamage(default=6) should" - {
      "be a valid instance of DieHit or DieMiss every time" in {
        iterations times {
          val result = DieResult.rollDamage()
          result match {
            case DieHit(_)  => dieHitAsserts(result.asInstanceOf[DieHit])
            case DieMiss(_) => dieMissAsserts(result.asInstanceOf[DieMiss])
            case _          => fail(s"Illegal DieResult type ($result) returned from DieResult.rollDamage")
          }
        }
      }
    }
  }
}