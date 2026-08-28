package it.unibo.parabellum
package model.entity

import model.shape.{Circle, Shape}
import util.Position

case class Soldier private(name: String, pos: Position, shape: Shape, state: State, facingDirection: Int) extends Figure:
  
  override def belongs(pos: Position): Boolean = shape.belongs(pos)
  def kill(): Unit =
    copy(state = State.dead)
    
  def isDead: Boolean =
    state == State.dead

  def isAlive: Boolean =
    state == State.alive
  
  override def toString: String =
    name
    

object Soldier:
  def initSoldier(name: String, pos: Position, ownerId: String, facingDirection: Int): Soldier =
    Soldier(name,
      pos,
      Circle(pos, SOLDIER_RADIUS),
      State.alive,
      facingDirection)

  private val SOLDIER_RADIUS: Double = 0.50
