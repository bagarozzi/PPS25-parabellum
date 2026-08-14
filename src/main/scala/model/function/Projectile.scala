package it.unibo.parabellum
package model.function

import util.Position

import it.unibo.parabellum.model.{ImpactEffect, Trajectory}


trait Projectile:
    def position: Position
    def previousPosition: Position
    def update(dt: Double): Projectile
    def impactEffect: ImpactEffect
    def vel: Double = 0.1

case class BasicProjectile(
  position: Position,
  previousPosition: Position,
  trajectory: Trajectory,
  impactEffect: ImpactEffect
) extends Projectile:

  override def update(dt: Double): Projectile =
    val newX = previousPosition.x + vel * dt
    BasicProjectile(trajectory.compute(newX), position, trajectory, impactEffect)