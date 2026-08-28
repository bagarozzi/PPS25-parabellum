package it.unibo.parabellum
package model.function

import util.Position
import model.function.Trajectory
import model.function.FunctionParser
import model.collision.ImpactEffect
import model.collision.ImpactEffect.{normalImpactEffect, piercingImpactEffect, ricochetImpactEffect}
import model.function

import it.unibo.parabellum.model.entity.{Burden, Piercing, PowerUp, Random, Ricochet}

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

  def changeFunction(function: Function): Projectile =
    this.copy(trajectory = Trajectory.create(pos(), function))


object Projectile:
  
  def createProjectile(startingPosition: Position, nonParsedFunction: String, direction: Int, powerUp: Option[PowerUp]): Projectile =
    val func: Function = FunctionParser.parse(nonParsedFunction) match
      case Right(func) => func
      case Left(e) => Function(x => x)
    powerUp match
      case None => createModifiedProjectile(normalImpactEffect(), startingPosition, direction, func)
      case Some(Piercing) => createModifiedProjectile(piercingImpactEffect(), startingPosition, direction, func)
      case Some(Burden) => createModifiedProjectile(normalImpactEffect(), startingPosition, direction, Function(x => func(x) - (0.05 * x * x)))
      case Some(Random) => createModifiedProjectile(normalImpactEffect(), startingPosition, direction, Function(x => math.random()*100*func(x)))
      case Some(Ricochet) => createModifiedProjectile(ricochetImpactEffect(), startingPosition, direction, func)
      case Some(_) => createModifiedProjectile(normalImpactEffect(), startingPosition, direction, func)
    
  private def createModifiedProjectile(impactEffect: ImpactEffect, startingPosition: Position, direction: Int, func: Function): Projectile =
    Projectile(
      Trajectory.create(startingPosition, func),
      0.0,
      0.01,
      impactEffect,
      direction
    )

