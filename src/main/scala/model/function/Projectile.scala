package it.unibo.parabellum
package model.function

import util.Position
import model.function.Trajectory
import model.function.FunctionParser

import it.unibo.parabellum.model.collision.ImpactEffect
import it.unibo.parabellum.model.collision.ImpactEffect.normalImpactEffect
import it.unibo.parabellum.model.entity.Entity
import it.unibo.parabellum.model.function

trait Projectile extends Entity:

    def update(dt: Double): Projectile

    def effect: ImpactEffect

    def pos: Position

private case class ProjectileI(trajectory: Trajectory, effect: ImpactEffect) extends Projectile:

  def update(dt: Double): Projectile =
    copy(
      trajectory = trajectory.update(dt)
    )

  val pos: Position = trajectory.currentPosition

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
      ProjectileI(
          Trajectory.create(startingPosition, func, if direction > 0 then Direction.Positive else Direction.Negative),
          normalImpactEffect()
      )