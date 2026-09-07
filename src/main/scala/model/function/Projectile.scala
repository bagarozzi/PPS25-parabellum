package it.unibo.parabellum
package model.function

import util.Position
import model.function.Trajectory

import model.collision.ImpactEffect
import model.entity.{Entity, Soldier, Player, PowerUp}
import model.function

/**
 * A [[Projectile]] is an [[Entity]] that is shot from a player
 * and eventually hits something in the map.
 */
trait Projectile extends Entity:

    def update(dt: Double): Projectile

    def effect: ImpactEffect

    def pos: Position

    def mapTrajectory(op: Trajectory => Trajectory): Projectile

    def trajectory: Trajectory

private case class ProjectileI(trajectory: Trajectory, effect: ImpactEffect) extends Projectile:

  def update(dt: Double): Projectile =
    copy(
      trajectory = trajectory.update(dt)
    )

  val pos: Position = trajectory.currentPosition

  def mapTrajectory(op: Trajectory => Trajectory): Projectile =
    this.copy(trajectory = op(trajectory))

object Projectile:

  def createProjectile(startingPosition: Position, function: Function, direction: Int, impactEffect: ImpactEffect, powerUp: Option[PowerUp]): Projectile =
    createModifiedProjectile(powerUp.fold(impactEffect)(_.impactEffect), startingPosition, direction, powerUp.fold(function)(_.trajectoryDistortion(function)))

  def fromSoldier(p: Player, s: Soldier, function: Function): Projectile = createProjectile(s.pos, function, s.facingDirection, p.impactEffect, p.getPowerUp)

  private def createModifiedProjectile(impactEffect: ImpactEffect, startingPosition: Position, direction: Int, func: Function): Projectile =
    ProjectileI(
      Trajectory.create(startingPosition, func, if direction > 0 then Direction.Positive else Direction.Negative),
        impactEffect
    )