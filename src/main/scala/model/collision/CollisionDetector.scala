package it.unibo.parabellum
package model.collision

import controller.GameState
import model.entity.{Entity, Figure, Obstacle, Player}
import model.function.Projectile
import util.{BoundingBox, Position}
import model.entity.Soldier

object CollisionDetector:
  val border: BoundingBox = BoundingBox(-25, 25, -15, 15)
  given BoundingBox = border

  private def checkCollisionWithBorders(projectile: Projectile): Option[Projectile] = 
    if !CollisionDetector.border.checkBoundary(projectile.pos()) then
      return None
    Some(projectile)


  private def CheckCollisionWithFigure(entities: Set[Figure], projectile: Projectile): Unit = 
    entities.filter(f => f.belongs(projectile.pos())).foreach {
      case soldier: Soldier => soldier.kill()
      case obstacle: Obstacle =>
    }

  def detectCollision(projectile: Projectile, entities: Set[Figure]): Option[Projectile] = 
    CheckCollisionWithFigure(entities, projectile)
    checkCollisionWithBorders(projectile) match
      case None => return None
      case toReturn => return toReturn
  
    