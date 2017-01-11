package mage

import org.scalatest.{FreeSpec, Matchers, Tag}

object Slow extends Tag("Slow")

abstract class UnitSpec extends FreeSpec with Matchers {
  val difficulty    = 6
  val iterations    = 10000
}