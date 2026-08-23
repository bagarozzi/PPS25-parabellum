package it.unibo.parabellum
package controller

import model.entity.{Obstacle, Player}

import it.unibo.parabellum.util.{Position, MapGenerator}
import it.unibo.parabellum.model.entity.Player.initPlayer
import model.function.{Projectile, Trajectory}
import it.unibo.parabellum.model.collision.CollisionDetector.detectCollision
import it.unibo.parabellum.model
import it.unibo.parabellum.model.collision.{CollisionDetector, ImpactEffect}
import it.unibo.parabellum.model.entity.State.dead
import it.unibo.parabellum.model.shape.{Circle, Polygon}
import model.entity.Soldier.initSoldier
import controller.TurnManager.initTunrManager
import model.entity.Figure

/**
 * Represents the state of the game in a certain instant in time.
 * @param manager the entity that manage the sequence of turns and the sets of soldiers
 * @param projectiles the projectile that are being fired
 */
class GameState(val manager: TurnManager,val obstacles: Set[Obstacle], val projectiles: Option[Projectile])
object GameState:

  /**
   * Updates the game state, returning a new state.
   * @param g the game state to update
   * @return the new game state
   */
  def update(g: GameState, dt: Double, pendingFunction: Option[String]): GameState = 
    val manager: TurnManager = g.manager.eliminateDeadSoldier
    
    val updatedProjectile: Option[Projectile] = g.projectiles.map(p => p.update(dt)).
      flatMap(p => detectCollision(p, manager.enemies ++ g.obstacles))

    val projectileToSpawn: Option[Projectile] = 
      if pendingFunction.isDefined then
        Some(Projectile.create(manager.current.pos, pendingFunction.get, manager.current.facingDirection))
      else
        updatedProjectile
    
    val nextManager = if updatedProjectile.isEmpty then
        manager.nextTurn
      else
        manager
    
    GameState(nextManager, g.obstacles, projectileToSpawn)

  def addObstacle(g: GameState, obstacle: Obstacle): GameState =
    GameState(g.manager, g.obstacles + obstacle, g.projectiles)

  def init(): GameState =
    val circle = Obstacle(Position(5.0, 3.0), 20.0)
    
    // TODO: make this resizable
    val minX = -10.0
    val maxX = 10.0
    val minY = -5.0
    val maxY = 5.0

 
    val manager = initTunrManager(MapGenerator.generatePlayers(minX, maxX, minY, maxY))
    val obstacles = MapGenerator.generateObstacles(5, minX, maxX, minY, maxY) // Scegli quanti ostacoli generare (es. 5)
  
    GameState(
    manager, obstacles+circle,
    None)