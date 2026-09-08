package it.unibo.parabellum
package model.collision


import controller.GameState
import model.entity.{Obstacle, PowerUp, Soldier}
import model.shape.Shape
import util.{BoundingBox, MapGenerator}

/**
 * An ImpactEvent represents consequences of an [[Impact]] on the game's state.
 */
trait ImpactEvent:

  /**
   * Apply the consequence of the impact to the game's state.
   * @param g the [[GameState]] where to apply the consequence
   * @return the new [[GameState]]
   */
  def action(g: GameState): GameState

case class KillSoldier(soldier: Soldier) extends ImpactEvent:

  override def action(g: GameState): GameState = g.copy(manager = g.manager.updateSoldier(soldier)(_ => None))

case class DamageObstacle(obstacle: Obstacle, hole: Shape) extends ImpactEvent:

  override def action(g: GameState): GameState = g.copy(obstacles = g.obstacles - obstacle + obstacle.addExplosion(hole))

case class DestroyProjectile() extends ImpactEvent:

  override def action(g: GameState): GameState = g.copy(manager = g.manager.nextTurn, projectile = None)

case class GainPowerUp(powerUp: PowerUp) extends ImpactEvent:

  override def action(g: GameState): GameState = g.copy(manager = g.manager.updatePlayer(g.manager.currentPlayer)(_.setPowerUp(Some(powerUp))), powerUps = g.powerUps - powerUp)

case class Ricochet() extends ImpactEvent:

  override def action(g: GameState): GameState = g.copy(projectile = g.projectile.map(_.mapTrajectory(_.ricochet())))

case class DestroyObstacle(obstacle: Obstacle) extends ImpactEvent:

  override def action(g: GameState): GameState = g.copy(obstacles = g.obstacles - obstacle)

case class SpawnNewObstacle() extends ImpactEvent:

  override def action(g: GameState): GameState =
    import controller.GameController.given
    MapGenerator.spawnObstacle(g)(using BoundingBox(0, border.x1, border.y0, border.y1))
