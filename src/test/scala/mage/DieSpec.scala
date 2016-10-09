package mage
/**
  * Created by Paul on 9/24/2016.
  */
import org.scalactic.TimesOnInt._
import org.scalatest.AsyncFreeSpec

class DieSpec extends AsyncFreeSpec {
  "A Die" - {
    "when rolled" - {
      "should not have a value < 1" in {
        assertThrows[IllegalArgumentException] {
          100 times { assert(Die.roll.result > 0) }
          Die(0)
        }
      }
      "should not have a value > 10" in {
        assertThrows[IllegalArgumentException] {
          100 times { assert(Die.roll.result < 11) }
          Die(11)
        }
      }
    }
    "whose value is 10 should" - {
      "be special" in { assert(Die(10).isSpecial) }
      "be successful" in { assert(Die(10).isSuccess()) }
      "not be a botch" in { assert(!Die(10).isBotch) }
    }
    "whose value is 1 should" - {
      "not be special" in { assert(!Die(1).isSpecial) }
      "not be successful" in { assert(!Die(1).isSuccess()) }
      "be a botch" in { assert(Die(1).isBotch) }
    }
  }
}
