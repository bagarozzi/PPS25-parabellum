package it.unibo.parabellum
package util

import org.scalatest.flatspec.AnyFlatSpec

class PlayerNamesSpec extends AnyFlatSpec:

  "PlayerNames" should "keep valid and different names untouched" in {
    assert(PlayerNames.normalize(Seq("Anna", "Luca")) == Vector("Anna", "Luca"))
  }

  it should "remove the spaces around the names" in {
    assert(PlayerNames.normalize(Seq("  Anna ", "Luca")) == Vector("Anna", "Luca"))
  }

  it should "replace blank names with a default one based on the position" in {
    assert(PlayerNames.normalize(Seq("", "   ", "Marco")) == Vector("Player 1", "Player 2", "Marco"))
  }

  it should "make duplicated names different" in {
    assert(PlayerNames.normalize(Seq("Anna", "Anna", "Anna")) == Vector("Anna", "Anna (2)", "Anna (3)"))
  }

  it should "not clash with names that look like the generated ones" in {
    assert(PlayerNames.normalize(Seq("Anna (2)", "Anna", "Anna")) == Vector("Anna (2)", "Anna", "Anna (3)"))
  }
