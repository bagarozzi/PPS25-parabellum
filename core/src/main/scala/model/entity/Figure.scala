package it.unibo.parabellum
package model.entity

import util.Position

import model.shape.Shape

trait Figure extends Entity:
  val shape: Shape
  def belongs(pos: Position): Boolean
