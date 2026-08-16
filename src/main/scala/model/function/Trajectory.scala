package it.unibo.parabellum
package model.function

import util.Position

trait Trajectory:
  def compute(x: Double): Double

class functionalTrajectory(startPosition: Position, f: Double => Double) extends Trajectory:
  override def compute(x: Double): Double =
    f(x)