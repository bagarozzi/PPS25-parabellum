package it.unibo.parabellum
package model.function

import util.Position
import model.function.Trajectory
import model.function.FunctionParser

import model.collision.ImpactEffect
import model.collision.ImpactEffect.{normalImpactEffect, ricochetImpactEffect}
import model.function

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

  def createProjectile(startingPosition: Position, nonParsedFunction: String, direction: Int): Projectile =
    val func: Function = FunctionParser.parse(nonParsedFunction) match
      case Right(func) => func
      case Left(e) => Function(x => x)
    Projectile(
      Trajectory.create(startingPosition, func),
      0.0,
      0.01,
      normalImpactEffect(), 
      direction
    )
      
  def createRicochetProjectile(startingPosition: Position, nonParsedFunction: String, direction: Int): Projectile =
    val func: Function = FunctionParser.parse(nonParsedFunction) match
      case Right(func) => func
      case Left(e) => Function(x => x)
    Projectile(
      Trajectory.create(startingPosition, func),
      0.0,
      0.01,
      ricochetImpactEffect(),
      direction
    )
    
  