package it.unibo.parabellum
package controller

import model.entity.Player

import it.unibo.parabellum.model.function.Projectile

/**
 * Represents the state of the game in a certain instant in time.
 * @param players the set of players currently in the game
 * @param projectiles the projectile that are being fired
 */
class GameState(val players: Set[Player], val projectiles: Set[Projectile])

object GameState:

  /**
   * Updates the game state, returning a new state.
   * @param g the game state to update
   * @return the new game state
   */
  def update(g: GameState): GameState = ???

  def init(): GameState = GameState(Set(), Set())