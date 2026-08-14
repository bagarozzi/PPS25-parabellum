package it.unibo.parabellum
package model

import model.entity.{Entity, Player}

import it.unibo.parabellum.controller.GameState
import it.unibo.parabellum.model.function.Projectile

object CollisionDetector
  def detectCollision(gs: GameState): Unit = {
    val offendedPlayer: Option[Player] = gs.players.find(p => !p.equals(gs.currentTurn))
    if(gs.projectiles.isDefined && offendedPlayer.isDefined && offendedPlayer.get.belongs(gs.projectiles.get.position))
      offendedPlayer.get.kill()
  }
