package it.unibo.parabellum
package model.shape

import util.Position

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

  /**
   *
   * @param vertices Seq of Position that represent the vertices of the polygon
   * @return a Polygon created from vertices
   */
  def create(vertices: Seq[Position]): Polygon =
    Polygon(sortVertices(vertices))

  /**
   *
   * @param center center of the circumcircle
   * @param radios radios of the circumcircle
   * @param sides number of sides for the polygon
   * @return a regular polygon created from the circumcircle
   */
  def regular(center: Position, radios: Double, sides: Int): Polygon =
    create(
      (0 until sides).map { i =>
        val angle = 2 * math.Pi * i / sides
        Position(center.x + radios * math.cos(angle), center.y + radios * math.sin(angle))
      }
    )
