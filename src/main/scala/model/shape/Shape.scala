package it.unibo.parabellum
package model.shape

import util.{BoundingBox, Position}

/**
 * A Shape is an enclosed area where some point's membership can be verified.
 */
sealed trait Shape:
  
  def bounds: BoundingBox

  /**
   * Checks if the point belongs (is internal) to the shape.
   *
   * @param pos the position to check
   * @return
   */
  def belongs: Position => Boolean

  final def sample(step: Double): Seq[Position] =
    for
      i <- 0 to ((bounds.x1 - bounds.x0) / step).toInt
      j <- 0 to ((bounds.y1 - bounds.y0) / step).toInt
      x = bounds.x0 + i * step
      y = bounds.y0 + j * step
      p = Position(x, y)
      if belongs(p)
    yield p
    
case class Difference(a: Shape, b: Set[Shape]) extends Shape:

  override def belongs: Position => Boolean = {
    p =>
      a.belongs(p) && !b.map(_.belongs(p)).foldLeft(false)(_||_)
  }

  override def bounds: BoundingBox = ???


/**
 * A class representing a Circle with radius and center.
 *
 * @param center the center of the circle
 * @param radius the radius of the circle
 */
case class Circle(
                   center: Position,
                   radius: Double
                 ) extends Shape:

  override val belongs: Position => Boolean =
    p =>
      val dx = p.x - center.x
      val dy = p.y - center.y

      dx * dx + dy * dy <= radius * radius

  override val bounds: BoundingBox = BoundingBox(center.x - radius, center.x + radius, center.y - radius, center.y + radius)

  /**
   * Checks if the point belongs (is internal) to the shape.
   *
   * @param pos the position to check
   * @return
   */
  
/**
 * A class representing a Polygon made out of vertices.
 *
 * @param vertices delimitating the polygon
 */
case class Polygon(
                    vertices: Seq[Position]
                  ) extends Shape:

  // 1. Calcolo del Bounding Box trovando i minimi e massimi tra i vertici
  override val bounds: BoundingBox =
    val minX = vertices.map(_.x).min
    val maxX = vertices.map(_.x).max
    val minY = vertices.map(_.y).min
    val maxY = vertices.map(_.y).max
    BoundingBox(minX, maxX, minY, maxY)

  // 2. Algoritmo per capire se un punto p è dentro il poligono
  override val belongs: Position => Boolean =
    p =>
      var inside = false
      var j = vertices.length - 1

      for i <- vertices.indices do
        val vi = vertices(i)
        val vj = vertices(j)

        val intersect = ((vi.y > p.y) != (vj.y > p.y)) &&
          (p.x < (vj.x - vi.x) * (p.y - vi.y) / (vj.y - vi.y) + vi.x)

        if intersect then inside = !inside
        j = i

      inside