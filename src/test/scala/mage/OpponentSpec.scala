package mage

class OpponentSpec extends UnitSpec {
  "An Opponent" - {
    s"when instantiated with a value < 0 or > ${Opponent.maxSoak} should yield an IllegalArgumentException" in {
      assertThrows[IllegalArgumentException] { Opponent(-1) }
      assertThrows[IllegalArgumentException] { Opponent(Opponent.maxSoak + 1) }
    }
    s"when instantiated with a difficulty < 2 or > ${Die.dieBase} should yield an IllegalArgumentException" in {
      assertThrows[IllegalArgumentException] { Opponent(0, -1) }
      assertThrows[IllegalArgumentException] { Opponent(0,  0) }
      assertThrows[IllegalArgumentException] { Opponent(0,  1) }
      assertThrows[IllegalArgumentException] { Opponent(0,  Die.dieBase + 1) }
    }
  }
  "rollToSoak(soakDice, SoakDifficulty) should" - {
    "return a valid result for all valid input combinations" in {
      for (s <- 0 to Opponent.maxSoak) {
        for (d <- 2 to Die.dieBase) {
          val soak = Opponent(s, d).rollToSoak()
          soak should be >= 0
          soak should be <= s
        }
      }
    }
  }
}
