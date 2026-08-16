package it.unibo.parabellum
package model.collision

import controller.GameState
import model.entity.{Entity, Player}
import model.function.Projectile
import util.{BoundingBox, Position}

object CollisionDetector:
  val border: BoundingBox = BoundingBox(-15, 15, -25, 25)
  given BoundingBox = border

  def checkCollisionWithBorders(projectile: Projectile): Option[Projectile] = 
    if !CollisionDetector.border.checkBoundary(projectile.pos()) then
      return None
    Some(projectile)


  def CheckCollisionWithPlayer(offendedPlayers: Set[Player], projectile: Projectile): Unit = 
    offendedPlayers.filter(p => p.belongs(projectile.pos())).foreach(p => p.kill())
  

  def detectCollision(projectile: Projectile, offendedPlayers: Set[Player]): Option[Projectile] = 
    CheckCollisionWithPlayer(offendedPlayers, projectile)
    checkCollisionWithBorders(projectile) match
      case None => return None
      case toReturn => return toReturn
  
    