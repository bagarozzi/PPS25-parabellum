package it.unibo.parabellum
package model.function

import util.Position

trait Trajectory:
  def compute(x: Double): Position
  
  

object Trajectory:
  def create(startPosition: Position, function: Function): Trajectory = {
    val offset = Position(0, -function(startPosition.x))
    new Trajectory {
      override def compute(x: Double): Position = Position(x, function.apply(x)).traslate(offset)
    }
  }
    
          