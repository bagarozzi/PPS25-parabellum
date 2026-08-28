package it.unibo.parabellum
package model.function

import util.Position
import model.function.Trajectory
import model.function.FunctionParser

import it.unibo.parabellum.model.collision.ImpactEffect
import it.unibo.parabellum.model.collision.ImpactEffect.normalImpactEffect
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

  def createProjectile(
              startingPosition: Position,
              nonParsedFunction: String,
              direction: Int
            ):
      Projectile =
      val func: Function = FunctionParser.parse(nonParsedFunction) match
          case Right(func) => func
          case Left(e) => Function(x => x)
      Projectile(
      Trajectory.create(startingPosition, func),
          startingPosition.x,
      0.01,
      normalImpactEffect(), 
      direction
    )