package it.unibo.parabellum
package controller

import controller.TeamManager.initTurnManager
import model.collision.CollisionDetector.detectCollision
import model.collision.ImpactEffect.{normalImpactEffect, shootingRangeImpactEffect}
import model.entity.Player.initPlayer
import model.entity.Soldier.initSoldier
import model.entity.*
import model.function.{Direction, Function, Projectile, Shot}
import util.MapGenerator.{spawnObstacle, spawnPowerUp, spawnSoldier}
import util.{BoundingBox, MapGenerator, Position}


/**
 * Represents the state of the game in a certain instant in time.
 * @param manager the entity that manage the sequence of turns and the sets of soldiers
 * @param projectile the projectile that are being fired
 * @param pendingShot the shot chosen by the current player, waiting for the projectile in flight (if any) to end
 */
case class GameState(manager: TeamManager, obstacles: Set[Obstacle], powerUps: Set[PowerUp], projectile: Option[Projectile], pendingShot: Option[Shot]):

  def map[B](op: GameState => B): B = op(this)

  def winner: Option[Player] = manager.winner
object GameState:

  /**
   * Updates the game state, returning a new state.
   * @param g the game state to update
   * @return the new game state
   */
  def update(g: GameState, dt: Double, passedShot: Option[Shot])(using border: BoundingBox): GameState =
    updateProjectile(g, dt).fold(g)(p => g.copy(projectile = Some(p)))
        .map(resolveCollisions)
        .map(processPendingInput(_, passedShot))
        .map(spawnProjectile)

  private def updateProjectile(g: GameState, dt: Double): Option[Projectile] = g.projectile match
    case Some(p) => Some(p.update(dt))
    case None => None

  private def resolveCollisions(g: GameState)(using border: BoundingBox): GameState = g
      .projectile
      .map(detectCollision(_, g.manager.enemies ++ g.obstacles ++ g.powerUps))
      .map(_.foldLeft(g)((g,e) => e.action(g)))
      .getOrElse(g)

  private def spawnProjectile(g: GameState): GameState = (g.projectile, g.pendingShot) match
    case(None, Some(shot)) => g.copy(manager = g.manager.updatePlayer(g.manager.currentPlayer)(_.setPowerUp(None)), projectile = Some(Projectile.fromSoldier(g.manager.currentPlayer, g.manager.current, shot.function, shot.direction)), pendingShot = None)
    case _ => g

  private def processPendingInput(g: GameState, passedShot: Option[Shot]): GameState = (g.pendingShot, passedShot) match
    case(None, Some(shot)) => g.copy(pendingShot = Some(shot))
    case _ => g

  def addObstacle(g: GameState, obstacle: Obstacle): GameState =
    GameState(g.manager, g.obstacles + obstacle, g.powerUps, g.projectile, None)

  def addPowerUp(g: GameState, pu: PowerUp): GameState =
    g.copy(powerUps = g.powerUps + pu)

  def addPlayer(g: GameState, player: Player): GameState =
    g.copy(manager = TeamManager.addPlayer(g.manager, player))

  def addSoldier(g: GameState, playerName: String, soldier: Soldier): GameState =
    g.copy(manager = TeamManager.addSoldier(g.manager, playerName, soldier))

  def init(players: Seq[String], soldiers: Int)(using border: BoundingBox): GameState =

    val emptyManager = TeamManager(Vector.empty, 0)

    GameState(emptyManager, Set.empty, Set.empty, None, None)
      .map(MapGenerator.generateObstacles(5, _))
      .map(MapGenerator.generatePlayers(players, soldiers, _))
      .map(MapGenerator.generatePowerUps(3, _))

  def testInit(): GameState =
    GameState(
      initTurnManager(Map((initPlayer("giorgio", normalImpactEffect()), Vector(initSoldier("giorgio-1", Position(-7.5, 0), "giorgio", Direction.Positive))))),
      Set(),
      Set(Ricochet(Position(7.5, 0))),
      None,
      None
    )

  def initShootingRange(): GameState =
    import util.MapBorder.given
    val shapesSpawnArea: BoundingBox = BoundingBox(0, border.x1, border.y0, border.y1)
    val shootingRangeName = "Ryan"
    GameState(initTurnManager(Map((initPlayer(shootingRangeName, shootingRangeImpactEffect()), Vector.empty))), Set(), Set(), None, None)
        .map(spawnSoldier(_, shootingRangeName, Direction.Positive, border.x0, 0)(using shapesSpawnArea))
        .map(spawnPowerUp(_)(using shapesSpawnArea))
        .map(spawnObstacle(_)(using shapesSpawnArea))
