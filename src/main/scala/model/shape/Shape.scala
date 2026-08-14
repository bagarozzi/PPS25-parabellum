package it.unibo.parabellum
package model.shape

import util.Position

/**
 * A Shape is an enclosed area where some point's membership can be verified.
 */
case class BoundingBound(minX: Double, maxX: Double, minY: Double, maxY:Double)

sealed trait Shape:

  def function: Position => Boolean

  def bounds: BoundingBound

  /**
   * Checks if the point belongs (is internal) to the shape.
   *
   * @param pos the position to check
   * @return
   */
  final def belongs(pos: Position): Boolean =
    function(pos)

  final def sample(step: Double): Seq[Position] =
    for
      i <- 0 to ((bounds.maxX - bounds.minX) / step).toInt
      j <- 0 to ((bounds.maxY - bounds.minY) / step).toInt
      x = bounds.minX + i * step
      y = bounds.minY + j * step
      p = Position(x, y)
      if belongs(p)
    yield p
      

/**
 * A class representing a Circle with radius and center.
 * @param center the center of the circle
 * @param radius the radius of the circle
 */
case class Circle(
                   center: Position,
                   radius: Double
                 ) extends Shape:

  override val function: Position => Boolean =
    p =>
      val dx = p.x - center.x
      val dy = p.y - center.y

      dx * dx + dy * dy <= radius * radius

  override val bounds: BoundingBound = BoundingBound(center.x - radius, center.x + radius, center.y - radius, center.y + radius)

