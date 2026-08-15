package it.unibo.parabellum
package model

import model.entity.{Entity, Player}
import controller.GameState
import model.function.Projectile
import util.Position

import it.unibo.parabellum.util.BoundingBox

object CollisionDetector:
  given border: BoundingBox = BoundingBox(-15, 15, -25, 25)

def checkCollisionWithBorders(projectile: Projectile)(using border: BoundingBox): Option[Projectile] = {
  if border.checkBoundary(projectile.position) then
    return None
  Some(projectile)
}

def CheckCollisionWithPlayer(offendedPlayers: List[Player], projectile: Projectile): Unit = {
  offendedPlayers.filter(p => p.belongs(projectile.position)).foreach(p => p.kill())
}

def detectCollision(gs: GameState)(using border: BoundingBox): Option[Projectile] = {
    val offendedPlayer: List[Player] = List(gs.players.find(p => !p.equals(gs.currentTurn)).get)
    if gs.projectiles.isDefined then 
      val projectile = gs.projectiles.get
      checkCollisionWithBorders(projectile) match
        case None => return None
        case toReturn => return toReturn
      
      CheckCollisionWithPlayer(offendedPlayer, projectile)
    gs.projectiles
  }
    