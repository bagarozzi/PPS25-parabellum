package it.unibo.parabellum
package util

/**
 * A slice of the map, delimited by two rays that start from the center of the map.
 *
 * @param center     the point where the two rays start
 * @param startAngle the angle (in radians) of the first ray
 * @param endAngle   the angle (in radians) of the second ray, reached going counterclockwise from the first one
 * @param margin     the minimum distance that a point has to keep from the two rays to be inside the sector
 */
case class Sector(center: Position, startAngle: Double, endAngle: Double, margin: Double):

  /**
   * Checks whether a position is inside this sector, at least `margin` far from its two rays.
   * @param p the position to check
   */
  def contains(p: Position): Boolean =
    val dx = p.x - center.x
    val dy = p.y - center.y
    val offset = Sector.normalize(math.atan2(dy, dx) - startAngle)
    offset < endAngle - startAngle &&
      Sector.distanceToRay(dx, dy, startAngle) >= margin &&
      Sector.distanceToRay(dx, dy, endAngle) >= margin

object Sector:

  private val FullTurn = 2 * math.Pi

  /** Brings an angle in the range [0, 2*PI). */
  private def normalize(angle: Double): Double = ((angle % FullTurn) + FullTurn) % FullTurn

  /** The distance between a point (given relative to the center) and the ray that starts from the center at the given angle. */
  private def distanceToRay(dx: Double, dy: Double, angle: Double): Double =
    val ux = math.cos(angle)
    val uy = math.sin(angle)
    val along = dx * ux + dy * uy
    if along >= 0 then math.abs(dx * uy - dy * ux) else math.hypot(dx, dy)

/**
 * Divides a rectangular map into "pie slices" around its center.
 */
object PieSectors:

  private val Steps = 3600
  private val FullTurn = 2 * math.Pi

  /**
   * Divides the map into sectors that all have the same area.
   * Equal angles would not be fair on a rectangle (with three sectors the one pointing towards a short
   * side would be much smaller than the others), so the cuts are placed where the swept area
   * reaches the same fraction of the total.
   *
   * @param count      the number of sectors
   * @param border     the bounding box of the map
   * @param startAngle the angle (in radians) where the first sector starts
   * @param margin     the minimum distance from the lines that divide the sectors
   * @return the sectors, going counterclockwise from `startAngle`
   */
  def split(count: Int, border: BoundingBox, startAngle: Double, margin: Double): Vector[Sector] =
    require(count > 0, "At least one sector is needed")
    val center = Position((border.x0 + border.x1) / 2.0, (border.y0 + border.y1) / 2.0)
    val halfWidth = (border.x1 - border.x0) / 2.0
    val halfHeight = (border.y1 - border.y0) / 2.0
    val step = FullTurn / Steps

    // cumulative area of the map swept by a ray rotating from `startAngle`
    val cumulativeArea = (0 until Steps)
      .map(i => wedgeArea(startAngle + (i + 0.5) * step, step, halfWidth, halfHeight))
      .scanLeft(0.0)(_ + _)
    val total = cumulativeArea.last

    val cuts = (0 to count).map: k =>
      if k == 0 then 0.0
      else if k == count then FullTurn
      else angleReaching(total * k / count, cumulativeArea, step)

    cuts.zip(cuts.tail).map((from, to) => Sector(center, startAngle + from, startAngle + to, margin)).toVector

  /** The area of a thin wedge of the rectangle, centered at the given angle. */
  private def wedgeArea(angle: Double, step: Double, halfWidth: Double, halfHeight: Double): Double =
    val cos = math.max(math.abs(math.cos(angle)), 1e-9)
    val sin = math.max(math.abs(math.sin(angle)), 1e-9)
    val radius = math.min(halfWidth / cos, halfHeight / sin)
    0.5 * radius * radius * step

  /** The angle at which the cumulative area reaches the target, interpolating between two steps. */
  private def angleReaching(target: Double, cumulativeArea: Seq[Double], step: Double): Double =
    val i = cumulativeArea.indexWhere(_ >= target)
    val before = cumulativeArea(i - 1)
    val fraction = (target - before) / (cumulativeArea(i) - before)
    (i - 1 + fraction) * step
