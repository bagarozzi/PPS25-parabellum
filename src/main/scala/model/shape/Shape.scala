package it.unibo.parabellum
package model.shape

import util.Position

sealed trait Shape:

  def belongs(pos: Position): Boolean

case class Circle(center: Position, radius: Double) extends Shape:

  def belongs(pos: Position): Boolean =
    val dx = pos.x - center.x
    val dy = pos.y - center.y
    (dx * dx + dy * dy) <= (radius * radius)


