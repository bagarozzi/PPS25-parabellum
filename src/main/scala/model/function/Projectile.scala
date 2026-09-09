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

    /**
     * Creates a [[Projectile]] from the passed arguments
     * @param startingPosition the position where it will start moving from
     * @param function the mathematical [[Function]] that it will follow
     * @param direction the direction of travel of the [[Function]]
     * @param impactEffect the [[ImpactEffect]] that the Projectile will have
     * @param powerUp the eventual [[PowerUp]] that is to be applied to this Projectile
     * @return a new [[Projectile]]
     */
    def createProjectile(startingPosition: Position, function: Function, direction: Direction, impactEffect: ImpactEffect, powerUp: Option[PowerUp]): Projectile =
        ProjectileI(
            Trajectory.create(startingPosition, powerUp.fold(function)(_.trajectoryDistortion(function)), direction),
            powerUp.fold(impactEffect)(_.impactEffect)
        )

    /**
     * Convenience method that builds a [[Projectile]] using the
     * data from a [[Player]] and it's [[Soldier]].
     * @param p the [[Player]] shooting the Projectile
     * @param s the [[Soldier]] where the Projectile will start from
     * @param function the [[Function]] that the Projectile will follow
     * @return a new [[Projectile]]
     */
    def fromSoldier(p: Player, s: Soldier, function: Function): Projectile = createProjectile(s.pos, function, s.facingDirection, p.impactEffect, p.getPowerUp)