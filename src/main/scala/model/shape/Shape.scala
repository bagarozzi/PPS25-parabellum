package it.unibo.parabellum
package model.shape

import util.Position

/**
 * A Shape is an enclosed area where some point's membership can be verified.
 */
sealed trait Shape:

  /**
   * Checks if the point belongs (is internal) to the shape.
   * @param pos the position to check
   * @return
   */
  def belongs(pos: Position): Boolean

/**
 * A class representing a Circle with radius and center.
 * @param center the center of the circle
 * @param radius the radius of the circle
 */
case class Circle(center: Position, radius: Double) extends Shape:

  def belongs(pos: Position): Boolean =
    val dx = pos.x - center.x
    val dy = pos.y - center.y
    (dx * dx + dy * dy) <= (radius * radius)


