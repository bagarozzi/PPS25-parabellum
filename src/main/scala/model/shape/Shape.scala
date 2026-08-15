package it.unibo.parabellum
package model.shape

import util.{BoundingBox, Position}

/**
 * A Shape is an enclosed area where some point's membership can be verified.
 */
sealed trait Shape:

  def function: Position => Boolean

  def bounds: BoundingBox

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
      i <- 0 to ((bounds.x1 - bounds.x0) / step).toInt
      j <- 0 to ((bounds.y1 - bounds.y0) / step).toInt
      x = bounds.x0 + i * step
      y = bounds.y0 + j * step
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

  override val bounds: BoundingBox = BoundingBox(center.x - radius, center.x + radius, center.y - radius, center.y + radius)

