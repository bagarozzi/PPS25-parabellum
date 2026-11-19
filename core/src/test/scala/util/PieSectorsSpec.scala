package it.unibo.parabellum
package util

import org.scalatest.flatspec.AnyFlatSpec

class PieSectorsSpec extends AnyFlatSpec:

  private val border = BoundingBox(-25, 25, -15, 15)
  private val firstStart = math.Pi / 2

  // a regular grid of points covering the whole map
  private val points: Vector[Position] =
    (for
      i <- 0 until 500
      j <- 0 until 300
    yield Position(-25 + (i + 0.5) * 0.1, -15 + (j + 0.5) * 0.1)).toVector

  for count <- 2 to 4 do
    "PieSectors" should s"divide the map in $count sectors with the same area" in {
      val sectors = PieSectors.split(count, border, firstStart, 0.0)
      assert(sectors.size == count)
      sectors.foreach: sector =>
        val fraction = points.count(sector.contains).toDouble / points.size
        assert(math.abs(fraction - 1.0 / count) < 0.01, s"a sector covers $fraction of the map instead of ${1.0 / count}")
    }

  "PieSectors" should "never put a point in more than one sector" in {
    val sectors = PieSectors.split(4, border, firstStart, 2.0)
    assert(points.forall(p => sectors.count(_.contains(p)) <= 1))
  }

  it should "give the left half of the map to the first of two sectors" in {
    val sectors = PieSectors.split(2, border, firstStart, 0.0)
    assert(points.filter(sectors.head.contains).forall(_.x < 0))
    assert(points.filter(sectors(1).contains).forall(_.x > 0))
  }

  it should "keep the points inside a sector away from the lines that divide the sectors" in {
    val sectors = PieSectors.split(2, border, firstStart, 2.0)
    val covered = points.filter(p => sectors.exists(_.contains(p)))
    assert(covered.nonEmpty)
    assert(covered.forall(p => math.abs(p.x) >= 2.0 - 1e-9))
  }

  it should "reject a request for no sectors" in {
    assertThrows[IllegalArgumentException](PieSectors.split(0, border, firstStart, 0.0))
  }
