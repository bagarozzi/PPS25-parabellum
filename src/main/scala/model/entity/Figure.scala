package it.unibo.parabellum
package model.entity

import util.Position

trait Figure extends Entity:

  def belongs(pos: Position): Boolean
