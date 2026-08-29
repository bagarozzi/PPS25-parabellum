package it.unibo.parabellum
package model.function

import util.Position
import model.function.Trajectory
import model.function.FunctionParser
import model.collision.ImpactEffect
import model.collision.ImpactEffect.{normalImpactEffect, piercingImpactEffect, ricochetImpactEffect}
import model.function

import it.unibo.parabellum.model.collision.ImpactEffect
import it.unibo.parabellum.model.collision.ImpactEffect.normalImpactEffect
import it.unibo.parabellum.model.entity.Entity
import it.unibo.parabellum.model.function
import it.unibo.parabellum.model.entity.{Burden, Piercing, PowerUp, Random, Ricochet}

trait Projectile extends Entity:

    def update(dt: Double): Projectile

    def effect: ImpactEffect

    def pos: Position

    def swapFunction(function: Function): Projectile

    def trajectory: Trajectory

private case class ProjectileI(trajectory: Trajectory, effect: ImpactEffect) extends Projectile:

  def update(dt: Double): Projectile =
    copy(
      trajectory = trajectory.update(dt)
    )

  val pos: Position = trajectory.currentPosition

  def swapFunction(function: Function): Projectile =
    this.copy(trajectory = trajectory.changeFunction(function))

object Projectile:

  def createProjectile(startingPosition: Position, nonParsedFunction: String, direction: Int, powerUp: Option[PowerUp]): Projectile =
    val func: Function = FunctionParser.parse(nonParsedFunction) match
      case Right(func) => func
      case Left(e) => Function(x => x)
    powerUp match
      case None | Some(_) => createModifiedProjectile(normalImpactEffect(), startingPosition, direction, func)
      case Some(Piercing) => createModifiedProjectile(piercingImpactEffect(), startingPosition, direction, func)
      case Some(Burden) => createModifiedProjectile(normalImpactEffect(), startingPosition, direction, Function(x => func(x) - (0.05 * x * x)))
      case Some(Random) => createModifiedProjectile(normalImpactEffect(), startingPosition, direction, Function(x => math.random()*100*func(x)))
      case Some(Ricochet) => createModifiedProjectile(ricochetImpactEffect(), startingPosition, direction, func)

  private def createModifiedProjectile(impactEffect: ImpactEffect, startingPosition: Position, direction: Int, func: Function): Projectile =
    ProjectileI(
      Trajectory.create(startingPosition, func, if direction > 0 then Direction.Positive else Direction.Negative),
        impactEffect
    )