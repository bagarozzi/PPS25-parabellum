package it.unibo.parabellum
package model

import model.entity.{Entity, Player}
import controller.GameState
import model.function.Projectile
import util.Position

import it.unibo.parabellum.util.BoundingBox

object CollisionDetector
  val border: BoundingBox = BoundingBox(-15, 15, -25, 25)
  def detectCollision(gs: GameState): Option[Projectile] = {
    val offendedPlayer: Player = gs.players.find(p => !p.equals(gs.currentTurn)).get
    if gs.projectiles.isDefined then 
      val projectile = gs.projectiles.get
      if border.checkBoundary(projectile.position) then
        return None
      if(offendedPlayer.belongs(projectile.position)) then
        offendedPlayer.kill()
    gs.projectiles
  }
    