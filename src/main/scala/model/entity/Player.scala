package it.unibo.parabellum
package model.entity

import util.Position

import model.shape.Shape

trait Player extends Figure:
  val name: String

class PlayerImpl(val name: String, val pos: Position, val shape: Shape) extends Player:

  override def belongs(pos: Position): Boolean = shape.belongs(pos)