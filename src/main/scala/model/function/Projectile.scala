package it.unibo.parabellum
package model.function

import util.Position

import it.unibo.parabellum.model.collision.ImpactEffect

case class Projectile private (
                                startPosition: Position,
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
      Position(distance, trajectory.compute(distance)).traslate(startPosition)

object Projectile:

  def create(
              startPosition: Position,
              trajectory: Trajectory,
              direction: Int
            ): Projectile =
      Projectile(
      startPosition,
      trajectory,
      0.0,
      0.1,
      null, 
      direction
    )

  def parseStraightLine(startingPosition: Position, angularCoefficient: Double): Trajectory =
      functionalTrajectory(startingPosition, x => angularCoefficient * x)