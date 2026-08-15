package it.unibo.parabellum
package model.function

import util.Position

import it.unibo.parabellum.model.{ImpactEffect, Trajectory}


trait Projectile:
    def position: Position
    def update(dt: Double): Projectile
    def impactEffect: ImpactEffect
    def vel: Double = 0.1

case class BasicProjectile(
  position: Position,
  trajectory: Trajectory,
  impactEffect: ImpactEffect,
  direction: Int
) extends Projectile:

  override def update(dt: Double): Projectile =
    val newX = position.x + (vel * dt * direction)
    
    BasicProjectile(trajectory.compute(newX), trajectory, impactEffect, direction)