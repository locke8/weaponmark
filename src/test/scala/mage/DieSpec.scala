package mage

import org.scalactic.TimesOnInt._

class DieSpec extends UnitSpec {
  private val maxDie     = Die(Die.dieBase, difficulty)
  private val hitDie     = Die(difficulty, difficulty)
  private val missDie    = Die(difficulty - 1, difficulty)
  private val botchDie   = Die(1, difficulty)

  "A Die" - {
    "whose value is 10 should" - {
      "be special"     in { maxDie.isSpecial shouldBe true }
      "be successful"  in { maxDie.isSuccess shouldBe true }
      "not be a botch" in { maxDie.isBotch   shouldBe false }
    }
    s"whose value is $difficulty should" - {
      "not be special" in { hitDie.isSpecial shouldBe false }
      "be successful"  in { hitDie.isSuccess shouldBe true }
      "not be a botch" in { hitDie.isBotch   shouldBe false }
    }
    s"whose value is ${difficulty - 1} should" - {
      "not be special" in { missDie.isSpecial shouldBe false }
      "not be a botch" in { missDie.isBotch   shouldBe false }
      s"not be successful (by default=$difficulty)" in {
                            missDie.isSuccess shouldBe false }
    }
    "whose value is 1 should" - {
      "not be special"    in { botchDie.isSpecial shouldBe false }
      "not be successful" in { botchDie.isSuccess shouldBe false }
      "be a botch"        in { botchDie.isBotch   shouldBe true }
    }
    f"when instantiated $iterations%,d times via Die.roll() should" - {
      "be a valid Die every time" in {
        def validate(die: Die) = {
          die.isBotch && (die.value != 1)               shouldBe false
          die.isSuccess && (die.value < die.difficulty) shouldBe false
          die.isSpecial && (die.value != Die.dieBase)   shouldBe false
          die.value < (Die.dieBase + 1)                 shouldBe true
          die.value > 0                                 shouldBe true
        }
        iterations times { validate(Die.roll(difficulty)) }
      }
    }
    s"when instantiated with a value < 1 or > ${Die.dieBase} should yield an IllegalArgumentException" - {
      assertThrows[IllegalArgumentException] { Die(-1) }
      assertThrows[IllegalArgumentException] { Die( 0) }
      assertThrows[IllegalArgumentException] { Die(11) }
    }
    s"when instantiated with a difficulty < 2 or > ${Die.dieBase} should yield an IllegalArgumentException" - {
      assertThrows[IllegalArgumentException] { Die(10, -1) }
      assertThrows[IllegalArgumentException] { Die(10,  0) }
      assertThrows[IllegalArgumentException] { Die(10,  1) }
      assertThrows[IllegalArgumentException] { Die(10, 11) }
    }
  }
}