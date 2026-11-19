package it.unibo.parabellum
package model.entity

import model.shape.{Circle, Shape}
import util.Position

import it.unibo.parabellum.model.function.Direction

/**
 * A soldier of a team.
 * @param facingDirection the direction proposed to the player when this soldier is about to shoot:
 *                        the player is free to shoot in the opposite one
 */
case class Soldier private(name: String, pos: Position, shape: Shape, state: State, facingDirection: Direction) extends Figure:
  
  override def belongs(pos: Position): Boolean = shape.belongs(pos)
  
  override def toString: String =
    name
    

object Soldier:
  def initSoldier(name: String, pos: Position, ownerId: String, facingDirection: Direction): Soldier =
    Soldier(name,
      pos,
      Circle(pos, SOLDIER_RADIUS),
      State.alive,
      facingDirection)

  private val SOLDIER_RADIUS: Double = 0.50
