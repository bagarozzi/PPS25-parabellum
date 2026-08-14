package it.unibo.parabellum
package model

import model.entity.{Entity, Player}

import controller.GameState
import model.function.Projectile

object CollisionDetector
  def detectCollision(gs: GameState): Unit = {
    val offendedPlayer: Option[Player] = gs.players.find(p => !p.equals(gs.currentTurn))
    if gs.projectiles.isDefined && offendedPlayer.isDefined && offendedPlayer.get.belongs(gs.projectiles.get.position) then
      offendedPlayer.get.kill()
  }
