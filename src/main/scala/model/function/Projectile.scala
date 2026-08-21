package it.unibo.parabellum
package model.function

import util.Position
import model.function.Trajectory
import model.function.FunctionParser

import it.unibo.parabellum.model.collision.ImpactEffect
import it.unibo.parabellum.model.function

case class Projectile private (
                                trajectory: Trajectory,
                                distance: Double,
                                speed: Double,
                                effect: ImpactEffect,
                                direction: Int
                              ):
  def update(dt: Double): Projectile =
    copy(
      distance = distance + speed * dt * direction
    )
  
  def pos(): Position=
    trajectory.compute(distance)

object Projectile:

  def create(
              startingPosition: Position,
              nonParsedFunction: String,
              direction: Int
            ):
      Projectile =
      Projectile(
      Trajectory.create(startingPosition, FunctionParser.parse(nonParsedFunction)),
      0.0,
      0.1,
      null, 
      direction
    )

  def parseStraightLine(startingPosition: Position, angularCoefficient: Double): Trajectory =
    Trajectory.create(startingPosition, Function(x => angularCoefficient * x))