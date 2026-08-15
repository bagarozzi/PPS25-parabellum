package it.unibo.parabellum
package model.entity

import util.Position
import model.shape.{Circle, Shape}

trait Player extends Figure:
  val name: String

class PlayerImpl(val name: String, val pos: Position, val shape: Shape) extends Player:

  override def belongs(pos: Position): Boolean = shape.belongs(pos)

object Player:

  private val PLAYER_RADIUS: Double = 0.15

  def initPlayer(name: String, pos: Position): Player = PlayerImpl(name, pos, Circle(pos, PLAYER_RADIUS))