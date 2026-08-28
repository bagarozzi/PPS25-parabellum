package it.unibo.parabellum
package model.entity

import model.shape.{Circle, Shape}
import util.Position
import model.collision.ImpactEffect.*

import it.unibo.parabellum.model.collision.ImpactEffect


sealed trait PowerUp(pos: Position) extends Figure:
  val shape: Shape = Circle(pos, 0.2)
  override final def belongs(pos: Position): Boolean = shape.belongs(pos)

case class Ricochet(pos: Position) extends PowerUp(pos) 

case class Burden(pos: Position) extends PowerUp(pos)

case class Random(pos: Position) extends PowerUp(pos)

case class Piercing(pos: Position) extends PowerUp(pos)