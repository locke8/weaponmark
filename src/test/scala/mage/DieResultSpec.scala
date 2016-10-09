package mage
/**
  * Created by Paul on 9/24/2016.
  */
import org.scalatest.AsyncFreeSpec

class DieResultSpec extends AsyncFreeSpec {
  "A DieResult" - {
    "when successful should" - {
      "not be special" in { assert(!Success(Die(7)).roll.isSpecial) }
      "be successful" in { assert(Success(Die(7)).roll.isSuccess(6)) }
      "not be a botch" in { assert(!Success(Die(7)).roll.isBotch)}
    }
    "when special should" - {
      "be special" in { assert(Success(Die(10)).roll.isSpecial) }
      "be successful" in { assert(Success(Die(10)).roll.isSuccess(6)) }
      "not be a botch" in { assert(!Success(Die(10)).roll.isBotch) }
    }
    "when a botch should" - {
      "not be special" in { assert(!Success(Die(1)).roll.isSpecial) }
      "not be successful" in { assert(!Success(Die(1)).roll.isSuccess(6)) }
      "be a botch" in { assert(Success(Die(1)).roll.isBotch) }
    }
  }
}
