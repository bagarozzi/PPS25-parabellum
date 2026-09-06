package it.unibo.parabellum
package model.entity

import model.shape.{Circle, Shape}
import util.Position
import model.collision.ImpactEffect.*

import scala.util.Random as RandomGen

import it.unibo.parabellum.model.function.*
import it.unibo.parabellum.model.collision.ImpactEffect


sealed trait PowerUp(pos: Position) extends Figure:
  val shape: Shape = Circle(pos, 0.4)

  override final def belongs(pos: Position): Boolean = shape.belongs(pos)

  def impactEffect: ImpactEffect = normalImpactEffect()

  def trajectoryDistortion(function: Function): Function = function

case class Ricochet(pos: Position) extends PowerUp(pos):
  override def impactEffect: ImpactEffect = ricochetImpactEffect()

case class Burden(pos: Position) extends PowerUp(pos):

  private val distortFunction: Function = Function(x => 0.05 * x * x)

  override def trajectoryDistortion(function: Function): Function = function - distortFunction

case class Random(pos: Position) extends PowerUp(pos):

  private val randomFunctions: List[(()=>Double, Double => Function=>Function)] = List(
    (() => RandomGen.between(0.0, 2.0), d => f => f + Function(x => d * math.sin(x))),
    (() => RandomGen.between(0.1, 0.3), d => f => f * Function(x => d * math.sin(x)))
  )

  override def trajectoryDistortion(function: Function): Function = randomFunctions(RandomGen.nextInt(randomFunctions.length)) match
    case (d, distort) => distort(d())(function)

case class Piercing(pos: Position) extends PowerUp(pos):
  override def impactEffect: ImpactEffect = piercingImpactEffect()