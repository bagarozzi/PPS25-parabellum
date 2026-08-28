package it.unibo.parabellum
package controller

import model.entity.Obstacle

import util.MapGenerator
import model.function.{Function, Projectile}

import model.collision.CollisionDetector.detectCollision
import model.collision.{DamageObstacle, DestroyProjectile, KillSoldier, Ricochet}
import model.shape
import controller.TurnManager.initTunrManager


/**
 * Represents the state of the game in a certain instant in time.
 * @param manager the entity that manage the sequence of turns and the sets of soldiers
 * @param projectile the projectile that are being fired
 */
case class GameState(val manager: TurnManager, val obstacles: Set[Obstacle], val projectile: Option[Projectile], val pendingFunction: Option[String])
object GameState:

  /**
   * Updates the game state, returning a new state.
   * @param g the game state to update
   * @return the new game state
   */
  def update(g: GameState, dt: Double, pendingFunction: Option[String]): GameState =
    val updatedProjectile = g.projectile.map(p => p.update(dt))
    val updatedState = updatedProjectile.map(detectCollision(_, g.manager.enemies ++ g.obstacles)).
      map(_.foldLeft(g.copy(projectile = updatedProjectile))((g,i) => i match
        case KillSoldier(soldier) => g.copy(manager = g.manager.eliminateDeadSoldier(soldier))
        case DamageObstacle(obstacle, hole) => g.copy(obstacles = g.obstacles - obstacle + obstacle.addExplosion(hole))
        case DestroyProjectile() => g.copy(manager = g.manager.nextTurn, projectile = None)
        case Ricochet() => g.copy(projectile = Some(g.projectile.get.changeFunction(Function(x => - g.projectile.get.trajectory.compute(x).y))))
      )).getOrElse(g.copy(projectile = updatedProjectile))

    val newState = if pendingFunction.isDefined && g.pendingFunction.isEmpty then
       updatedState.copy(pendingFunction = pendingFunction)
    else
      updatedState

    if newState.projectile.isEmpty && newState.pendingFunction.isDefined then
      newState.copy(projectile = Some(Projectile.createRicochetProjectile(newState.manager.current.pos, newState.pendingFunction.get, newState.manager.current.facingDirection)), pendingFunction = None)
    else
      newState


  /*val manager: TurnManager = g.manager.eliminateDeadSoldier

  val updatedProjectile: Option[Projectile] = g.projectiles.map(p => p.update(dt)).
    flatMap(p => detectCollision(p, manager.enemies ++ g.obstacles))

  if pendingFunction.isDefined && updatedProjectile.isEmpty then
    val tmpManager = manager.nextTurn
    GameState(tmpManager, g.obstacles, Some(Projectile.createProjectile(tmpManager.current.pos, pendingFunction.get, tmpManager.current.facingDirection)), None)
  else
    GameState(manager, g.obstacles, updatedProjectile, pendingFunction)*/
    

  def addObstacle(g: GameState, obstacle: Obstacle): GameState =
    GameState(g.manager, g.obstacles + obstacle, g.projectile, None)

  def init(player1: String, player2: String, soldiers: Int): GameState =
    
    // TODO: make this resizable
    val minX = -10.0
    val maxX = 10.0
    val minY = -5.0
    val maxY = 5.0

 
    val manager = initTunrManager(MapGenerator.generatePlayers(minX, maxX, minY, maxY, player1, player2, soldiers))
    val obstacles = MapGenerator.generateObstacles(5, minX, maxX, minY, maxY) // Scegli quanti ostacoli generare (es. 5)
  
    GameState(
      manager,
      obstacles,
      None,
      None
    )