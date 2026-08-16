package it.unibo.parabellum
package model.entity

import model.shape.{Circle, Shape}
import util.Position

import it.unibo.parabellum.model.entity.Player.PLAYER_RADIUS
  
case class Soldier private(val name: String, val pos: Position, val shape: Shape, var state: State, val owner: Player, val facingDirection: Int):
  
  def kill(): Unit =
    copy(state = State.dead)
    
  def isDead: Boolean =
    state == State.dead

  def isAlive: Boolean =
    state == State.alive
  
    

object Soldier:
  def init(name: String, pos: Position, shape: Shape, owner: Player, facingDirection: Int): Soldier =
    Soldier(name,
      pos,
      Circle(pos, SOLDIER_RADIUS),
      State.alive, 
      owner,
      facingDirection)

  private val SOLDIER_RADIUS: Double = 0.15