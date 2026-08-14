package it.unibo.parabellum
package model.entity

import util.Position

import it.unibo.parabellum.model.shape.Shape

trait Figure extends Entity:

  val shape: Shape

  def belongs(pos: Position): Boolean
