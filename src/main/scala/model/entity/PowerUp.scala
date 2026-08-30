package it.unibo.parabellum
package model.entity

import model.shape.{Circle, Shape}
import util.Position
import model.collision.ImpactEffect.*

import it.unibo.parabellum.model.function.*
import it.unibo.parabellum.model.collision.ImpactEffect


sealed trait PowerUp(pos: Position) extends Figure:
  val shape: Shape = Circle(pos, 0.2)

  override final def belongs(pos: Position): Boolean = shape.belongs(pos)

  def impactEffect: ImpactEffect = normalImpactEffect()

  def trajectoryDistortion(function: Function): Function = function

case class Ricochet(pos: Position) extends PowerUp(pos):
  override def impactEffect: ImpactEffect = ricochetImpactEffect()

case class Burden(pos: Position) extends PowerUp(pos):

  private val distortFunction: Function = Function(x => 0.05 * x * x)

  override def trajectoryDistortion(function: Function): Function = function - distortFunction

case class Random(pos: Position) extends PowerUp(pos):

  private val distortFunction: Function = Function(x => math.random() * 100)

  override def trajectoryDistortion(function: Function): Function = distortFunction * function

case class Piercing(pos: Position) extends PowerUp(pos):
  override def impactEffect: ImpactEffect = piercingImpactEffect()