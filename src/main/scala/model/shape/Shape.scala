package it.unibo.parabellum
package model.shape

import util.Position

trait Shape:

  enum Type:
    case Circ(center: Position, radius: Double)
    case Rect(center: Position, width: Double, height: Double)

  def belongs(pos: Position): Boolean

  def getType: Type


