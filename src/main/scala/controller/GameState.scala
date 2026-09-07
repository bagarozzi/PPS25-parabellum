package it.unibo.parabellum
package controller

import controller.TeamManager.initTurnManager
import model.collision.CollisionDetector.detectCollision
import model.collision.ImpactEffect.{normalImpactEffect, shootingRangeImpactEffect}
import model.entity.Player.initPlayer
import model.entity.Soldier.initSoldier
import model.entity.*
import model.function.{Function, Projectile}
import util.MapGenerator.{spawnObstacle, spawnPowerUp, spawnSoldier}
import util.{BoundingBox, MapGenerator, Position}


/**
 * Represents the state of the game in a certain instant in time.
 * @param manager the entity that manage the sequence of turns and the sets of soldiers
 * @param projectile the projectile that are being fired
 */
case class GameState(val manager: TeamManager, val obstacles: Set[Obstacle], powerUps: Set[PowerUp], val projectile: Option[Projectile], val pendingFunction: Option[Function]):

  def map[B](op: GameState => B): B = op(this)

  def winner: Option[Player] = manager.winner
object GameState:

  /**
   * Updates the game state, returning a new state.
   * @param g the game state to update
   * @return the new game state
   */
  def update(g: GameState, dt: Double, passedFunction: Option[Function])(using border: BoundingBox): GameState =
    updateProjectile(g, dt).fold(g)(p => g.copy(projectile = Some(p)))
        .map(resolveCollisions)
        .map(processPendingInput(_, passedFunction))
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
    case(None, Some(func)) => g.copy(manager = g.manager.updatePlayer(g.manager.currentPlayer)(_.setPowerUp(None)), projectile = Some(Projectile.fromSoldier(g.manager.currentPlayer, g.manager.current, func)), pendingFunction = None)
    case _ => g

  private def processPendingInput(g: GameState, passedFunction: Option[Function]): GameState = (g.pendingFunction, passedFunction) match
    case(None, Some(pf)) => g.copy(pendingFunction = Some(pf))
    case _ => g

  def addObstacle(g: GameState, obstacle: Obstacle): GameState =
    GameState(g.manager, g.obstacles + obstacle, g.powerUps, g.projectile, None)

  def addPowerUp(g: GameState, pu: PowerUp): GameState =
    g.copy(powerUps = g.powerUps + pu)

  def addPlayer(g: GameState, player: Player): GameState =
    g.copy(manager = TeamManager.addPlayer(g.manager, player))

  def addSoldier(g: GameState, playerName: String, soldier: Soldier): GameState =
    g.copy(manager = TeamManager.addSoldier(g.manager, playerName, soldier))

  def init(players: Set[String], soldiers: Int)(using border: BoundingBox): GameState =

    val emptyManager = TeamManager(Vector.empty, 0)

    GameState(emptyManager, Set.empty, Set.empty, None, None)
      .map(MapGenerator.generateObstacles(5, _))
      .map(MapGenerator.generatePlayers(players, soldiers, _))
      .map(MapGenerator.generatePowerUps(3, _))

  def testInit(): GameState =
    GameState(
      initTurnManager(Map((initPlayer("giorgio", normalImpactEffect()), Vector(initSoldier("giorgio-1", Position(-7.5, 0), "giorgio", 1))))),
      Set(),
      Set(Ricochet(Position(7.5, 0))),
      None,
      None
    )

  def initShootingRange(): GameState =
    import controller.GameController.given
    val shapesSpawnArea: BoundingBox = BoundingBox(0, border.x1, border.y0, border.y1)
    val shootingRangeName = "Ryan"
    GameState(initTurnManager(Map((initPlayer(shootingRangeName, shootingRangeImpactEffect()), Vector.empty))), Set(), Set(), None, None)
        .map(spawnSoldier(_, shootingRangeName, 1, border.x0, 0)(using shapesSpawnArea))
        .map(spawnPowerUp(_)(using shapesSpawnArea))
        .map(spawnObstacle(_)(using shapesSpawnArea))
