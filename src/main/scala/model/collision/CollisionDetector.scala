package it.unibo.parabellum
package model.collision

import controller.GameState
import model.entity.{Entity, Figure, Obstacle, Player}
import model.function.Projectile
import util.{BoundingBox, Position}
import model.entity.Soldier
import model.collision.ImpactEffect

object CollisionDetector:

  private def checkCollisionWithBorders(projectile: Projectile)(using border: BoundingBox): Option[Impact] =
    if !border.checkBoundary(projectile.pos) then
      return Some(BorderImpact())
    None


  private def CheckCollisionWithFigure(entities: Set[Figure], projectile: Projectile): Option[Impact] = 
    entities.find(f => f.belongs(projectile.pos)).map(FigureImpact(projectile.pos, _))

  def detectCollision(projectile: Projectile, entities: Set[Figure])(using border: BoundingBox): Set[ImpactEvent] =
    (CheckCollisionWithFigure(entities, projectile) ++ checkCollisionWithBorders(projectile)).flatMap(projectile.effect.applyEffect).toSet
    