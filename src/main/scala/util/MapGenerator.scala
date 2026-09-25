package it.unibo.parabellum
package util

import model.entity.{Burden, Figure, Obstacle, Piercing, PowerUp, Random, Ricochet}
import model.entity.Player.initPlayer
import model.entity.Soldier.*
import controller.GameState

import scala.annotation.tailrec
import model.shape.Circle as ModelCircle
import model.collision.ImpactEffect.normalImpactEffect

import it.unibo.parabellum.model.function.Direction

/**
 * Utility object responsible for the procedural generation of the game map.
 * It uses a functional State pattern (GameState => GameState) to ensure immutability,
 * where each generation step reads the occupied spaces from the current state and returns an updated one.
 */
object MapGenerator:
  private val defaultObs = 4.0
  private val defaultPu = 0.4
  private val defaultSoldier = 0.5
  /**
   * Dynamically extracts the spatial footprint (X, Y, Radius) of all entities
   * currently present in the given GameState.
   *
   * @param g The current GameState.
   * @return A sequence of tuples representing the occupied areas to be passed to Prolog.
   */
  private def getOccupiedSpaces(g: GameState): Seq[(Double, Double, Double)] =
    transform(g.obstacles, defaultObs) ++ transform(g.powerUps, defaultPu) ++ transform(g.manager.soldiers, defaultSoldier)

  private def transform[C <: Figure](set: Set[C], defRad: Double): Seq[(Double, Double, Double)] =
    val out = for
      o <- set
    yield extractDimensions(o, defRad)
    out.toSeq

  private def extractDimensions(element: Figure, defaultRadius: Double): (Double, Double, Double) =
    (element.pos.x, element.pos.y, getRadius(element, defaultRadius))

  private def getRadius(element: Figure, defaultRadius: Double): Double = element.shape match
    case ModelCircle(_, radius) => radius
    case _ => defaultRadius




  /**
   * Spawns a single random obstacle (Circle or Polygon) avoiding collisions.
   * Uses tail recursion to retry upon overlap detection.
   *
   * @param g      The current GameState.
   * @param border The implicit bounding box of the map.
   * @return The updated GameState containing the new obstacle.
   */
  @tailrec
  def spawnObstacle(g: GameState)(using border: BoundingBox): GameState =
    val pos = RandomGenerator.randomPosition(border.x0, border.x1, border.y0, border.y1)
    val isCircle = math.random() > 0.5
    val maxRadius = if isCircle then 0.5 + math.random() else defaultObs + math.random()

    if PrologMapChecker.hasOverlap(pos.x, pos.y, maxRadius, getOccupiedSpaces(g)) then
      spawnObstacle(g)
    else
      val newObstacle = if isCircle then Obstacle.setCircle(pos, maxRadius)
      else
        val numVertices = 3 + (math.random() * 4).toInt
        val vertices = (1 to numVertices).map: _ =>
          RandomGenerator.randomPosition(pos.x - maxRadius, pos.x + maxRadius, pos.y - maxRadius, pos.y + maxRadius)
        Obstacle.setPolygon(pos, vertices)

      GameState.addObstacle(g, newObstacle)


  /**
   * Spawns a single random power-up in a free space.
   *
   * @param g      The current GameState.
   * @param border The implicit bounding box of the map.
   * @return The updated GameState containing the new power-up.
   */
  @tailrec
  def spawnPowerUp(g: GameState)(using border: BoundingBox): GameState =
    val pX = border.x0 + (border.x1 - border.x0) * math.random()
    val pY = border.y0 + (border.y1 - border.y0) * math.random()
    val pos = Position(pX, pY)

    val rand = math.random()
    val pu: PowerUp =
      if rand < 0.25 then Ricochet(pos)
      else if rand < 0.50 then Burden(pos)
      else if rand < 0.75 then Random(pos)
      else Piercing(pos)

    if PrologMapChecker.hasOverlap(pX, pY, getRadius(pu, defaultPu), getOccupiedSpaces(g)) then
      spawnPowerUp(g)
    else
      GameState.addPowerUp(g, pu)


  /**
   * Spawns a single soldier for a specific team within the designated map segment.
   *
   * @param g          The current GameState.
   * @param playerName The name of the player owning this soldier.
   * @param direction  The facing direction of the soldier (1 for right, -1 for left).
   * @param spawnMinX  The minimum X boundary for this specific team's spawn area.
   * @param spawnMaxX  The maximum X boundary for this specific team's spawn area.
   * @param border     The implicit bounding box of the map.
   * @return The updated GameState containing the newly assigned soldier.
   */
  @tailrec
  def spawnSoldier(g: GameState, playerName: String, direction: Direction, spawnMinX: Double, spawnMaxX: Double)(using border: BoundingBox): GameState =
    val pX = spawnMinX + (spawnMaxX - spawnMinX) * math.random()
    val pY = border.y0 + (border.y1 - border.y0) * math.random()

    val teamCurrentSize = g.manager.teams.find(_.owner.name == playerName).map(_.soldiers.size).getOrElse(0)
    val newSoldier = initSoldier(s"$playerName-soldier${teamCurrentSize + 1}", Position(pX, pY), playerName, direction)

    if PrologMapChecker.hasOverlap(pX, pY, getRadius(newSoldier, defaultSoldier), getOccupiedSpaces(g)) then
      spawnSoldier(g, playerName, direction, spawnMinX, spawnMaxX)
    else
      GameState.addSoldier(g, playerName, newSoldier)



  /**
   * Generates a specified number of obstacles by folding over the GameState.
   *
   * @param count            The amount of obstacles to generate.
   * @param initialGameState The starting GameState.
   * @return The resulting GameState populated with obstacles.
   */
  def generateObstacles(count: Int, initialGameState: GameState)(using border: BoundingBox): GameState =
    (1 to count).foldLeft(initialGameState)((acc, _) => spawnObstacle(acc))

  /**
   * Generates a specified number of power-ups by folding over the GameState.
   *
   * @param count            The amount of power-ups to generate.
   * @param initialGameState The starting GameState.
   * @return The resulting GameState populated with power-ups.
   */
  def generatePowerUps(count: Int, initialGameState: GameState)(using border: BoundingBox): GameState =
    (1 to count).foldLeft(initialGameState)((acc, _) => spawnPowerUp(acc))

  /**
   * Divides the map, initializes the players, and generates their respective teams of soldiers.
   *
   * @param players          A Set containing the names of the players.
   * @param soldierCount     The number of soldiers per team.
   * @param initialGameState The starting GameState (usually already containing obstacles).
   * @return The resulting GameState populated with players and soldiers.
   */
  def generatePlayers(players: Set[String], soldierCount: Int, initialGameState: GameState)(using border: BoundingBox): GameState =
    val midX = (border.x0 + border.x1) / 2.0
    val safeMargin = 2.0

    players.toList.zipWithIndex.foldLeft(initialGameState): (stateAcc, playerWithIndex) =>
      val (playerName, index) = playerWithIndex
      val isLeft = index == 0
      val spawnMinX = if isLeft then border.x0 else midX + safeMargin
      val spawnMaxX = if isLeft then midX - safeMargin else border.x1
      val direction = if isLeft then Direction.Positive else Direction.Negative

      val stateWithPlayer = GameState.addPlayer(stateAcc, initPlayer(playerName, normalImpactEffect()))

      (1 to soldierCount).foldLeft(stateWithPlayer): (teamAcc, _) =>
        spawnSoldier(teamAcc, playerName, direction, spawnMinX, spawnMaxX)