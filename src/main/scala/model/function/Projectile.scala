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
    Position(startPosition.x + distance, startPosition.y + trajectory.compute(distance))

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