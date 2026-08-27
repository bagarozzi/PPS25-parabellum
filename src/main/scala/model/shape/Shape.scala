package it.unibo.parabellum
package model.shape

import util.Position

/**
 * A Shape is an enclosed area where some point's membership can be verified.
 */
sealed trait Shape:

  /**
   * Checks whether a position belongs to the shape
   * @return
   */
  def belongs: Position => Boolean
    
case class Difference(a: Shape, b: Set[Shape]) extends Shape:

  override def belongs: Position => Boolean =
    p => a.belongs(p) && !b.map(_.belongs(p)).foldLeft(false)(_||_)

  def diffSet: Set[Shape] =
    def findDiffSet(a: Shape): Set[Shape] = a match
      case Difference(c, d) => (d + c).flatMap(findDiffSet)
      case s => Set(s)
    b.flatMap(findDiffSet)

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

/**
 * A class representing a Polygon made out of vertices.
 *
 * @param vertices delimitating the polygon
 */
case class Polygon private(
                    vertices: Seq[Position]
                  ) extends Shape:

  // 2. Algoritmo per capire se un punto p è dentro il poligono
  private val edges =
    vertices.zip(
      vertices.tail :+ vertices.head
    )

  override def belongs: Position => Boolean =
    p =>
      edges.count { (a, b) =>
        ((a.y > p.y) != (b.y > p.y)) &&
          (p.x < (b.x - a.x) * (p.y - a.y) / (b.y - a.y) + a.x)
      } % 2 == 1

object Polygon:

  private def sortVertices(vertices: Seq[Position]): Seq[Position] =
    val center = Position(
      vertices.map(_.x).sum / vertices.size,
      vertices.map(_.y).sum / vertices.size
    )
    vertices.sortBy { v =>
      math.atan2(
        v.y - center.y,
        v.x - center.x
      )
    }

  def create(vertices: Seq[Position]): Polygon =
    Polygon(sortVertices(vertices))
