package it.unibo.parabellum
package controller

import model.entity.Player

import it.unibo.parabellum.model.function.Projectile
import it.unibo.parabellum.util.Position
import it.unibo.parabellum.model.entity.Player.initPlayer

/**
 * Represents the state of the game in a certain instant in time.
 * @param players the set of players currently in the game
 * @param projectiles the projectile that are being fired
 */
class GameState(val players: Set[Player], val projectiles: Option[Projectile], val currentTurn: Player)

object GameState:

  /**
   * Updates the game state, returning a new state.
   * @param g the game state to update
   * @return the new game state
   */
  def update(g: GameState): GameState = ???

  def init(): GameState =
    val players = Set(initPlayer("player1", Position(-7.5, 0.0)), initPlayer("player2", Position(7.5, 0.0)))
    GameState(
    players,
    None,
    players.head)