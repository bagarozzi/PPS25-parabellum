package it.unibo.parabellum
package model

import util.Position

class Trajectory(m: Double, q: Double):

  def compute(x: Double): Position =
    Position(x, m*x+q)
