package it.unibo.parabellum
package controller

import model.entity.{Obstacle, PowerUp}
import util.{BoundingBox, MapGenerator}
import model.function.Projectile
import model.collision.CollisionDetector.detectCollision
import model.collision.ImpactEvent
import controller.TurnManager.initTurnManager


/**
 * Represents the state of the game in a certain instant in time.
 * @param manager the entity that manage the sequence of turns and the sets of soldiers
 * @param projectile the projectile that are being fired
 */
case class GameState(val manager: TurnManager, val obstacles: Set[Obstacle], powerUps: Set[PowerUp], val projectile: Option[Projectile], val pendingFunction: Option[String]):

  def map[B](op: GameState => B): B = op(this)
object GameState:

  /**
   * Updates the game state, returning a new state.
   * @param g the game state to update
   * @return the new game state
   */
  def update(g: GameState, dt: Double, pendingFunction: Option[String])(using border: BoundingBox): GameState =
    updateProjectile(g, dt).fold(g)(p => g.copy(projectile = Some(p)))
        .map(resolveCollisions)
        .map(processPendingInput(_, pendingFunction))
        .map(spawnProjectile)

  private def updateProjectile(g: GameState, dt: Double): Option[Projectile] = g.projectile match
    case Some(p) => Some(p.update(dt))
    case None => None

  private def resolveCollisions(g: GameState)(using border: BoundingBox): GameState = g
      .projectile
      .map(detectCollision(_, g.manager.enemies ++ g.obstacles ++ g.powerUps))
      .map(_.foldLeft(g)((g,e) => e.action(g)))
      .getOrElse(g)

  private def spawnProjectile(g: GameState): GameState = (g.projectile, g.pendingFunction) match
    case(None, Some(func)) => g.copy(projectile = Some(Projectile.fromSoldier(g.manager.currentPlayer, g.manager.current, func)), pendingFunction = None)
    case _ => g

  private def processPendingInput(g: GameState, passedFunction: Option[String]): GameState = (g.pendingFunction, passedFunction) match
    case(None, Some(pf)) => g.copy(pendingFunction = Some(pf))
    case _ => g

  def addObstacle(g: GameState, obstacle: Obstacle): GameState =
    GameState(g.manager, g.obstacles + obstacle, g.powerUps, g.projectile, None)

  def init(players: Set[String], soldiers: Int)(using border: BoundingBox): GameState =
    
    // TODO: make this resizable
    val minX = -10.0
    val maxX = 10.0
    val minY = -5.0
    val maxY = 5.0


    val (obstacles, data1) = MapGenerator.generateObstacles(5)
    val (playersMap, data2) = MapGenerator.generatePlayers(players, soldiers, data1)
    val manager = initTurnManager(playersMap)
    val (powerUps, finalData) = MapGenerator.generatePowerUps(3, data2)
  
    GameState(
      manager,
      obstacles,
      powerUps,
      None,
      None
    )