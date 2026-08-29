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
import it.unibo.parabellum.model.entity.{Burden, Entity, Piercing, Soldier, Player, PowerUp, Random, Ricochet}
import it.unibo.parabellum.model.function

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
    createModifiedProjectile(powerUp.fold(normalImpactEffect())(_.impactEffect), startingPosition, direction, powerUp.fold(func)(_.trajectoryDistortion(func)))

  def fromSoldier(p: Player, s: Soldier, nonParsedFunction: String): Projectile = createProjectile(s.pos, nonParsedFunction, s.facingDirection, p.getPowerUp)

  private def createModifiedProjectile(impactEffect: ImpactEffect, startingPosition: Position, direction: Int, func: Function): Projectile =
    ProjectileI(
      Trajectory.create(startingPosition, func, if direction > 0 then Direction.Positive else Direction.Negative),
        impactEffect
    )