package it.unibo.parabellum
package model.entity

import util.Position
import model.shape.{Circle, Shape}

import it.unibo.parabellum.model.function.Trajectory

trait Player extends Figure:
  val name: String
  def state: State
  def kill(): Unit
  def shoot(trajectory: Trajectory): Unit

enum State:
  case alive, dead
  
class PlayerImpl(val name: String, val pos: Position, val shape: Shape, var state: State) extends Player:

  override def belongs(pos: Position): Boolean = shape.belongs(pos)
  def kill(): Unit = {
    //TODO implement this method
    println(name + "has been eliminated")
  }

  override def shoot(trajectory: Trajectory): Unit = ???

object Player:

  private val PLAYER_RADIUS: Double = 0.15

  def initPlayer(name: String, pos: Position): Player = PlayerImpl(name, pos, Circle(pos, PLAYER_RADIUS), State.alive)
  
  def shoot(trajectory: Trajectory): Unit = ???