package it.unibo.parabellum
package util

import model.entity.{Obstacle, Player, Soldier, PowerUp, Ricochet, Burden, Random, Piercing}
import model.entity.Player.initPlayer
import model.entity.Soldier.*
import controller.GameState
import scala.annotation.tailrec
import model.shape.{Circle => ModelCircle}

/**
 * Utility object responsible for the procedural generation of the game map.
 * It uses a functional State pattern (GameState => GameState) to ensure immutability,
 * where each generation step reads the occupied spaces from the current state and returns an updated one.
 */
object MapGenerator:

  /**
   * Dynamically extracts the spatial footprint (X, Y, Radius) of all entities
   * currently present in the given GameState.
   *
   * @param g The current GameState.
   * @return A sequence of tuples representing the occupied areas to be passed to Prolog.
   */
  private def getOccupiedSpaces(g: GameState): Seq[(Double, Double, Double)] =
    val obsSpaces = g.obstacles.toSeq.map: o =>
      val r = o.shape match
        case ModelCircle(_, radius) => radius
        case _ => 4.0
      (o.pos.x, o.pos.y, r)

    val puSpaces = g.powerUps.toSeq.map: pu =>
      val r = pu.shape match
        case ModelCircle(_, radius) => radius
        case _ => 0.2
      (pu.pos.x, pu.pos.y, r)

    val soldierSpaces = g.manager.soldiers.toSeq.map: s =>
      val r = s.shape match
        case ModelCircle(_, radius) => radius
        case _ => 0.15
      (s.pos.x, s.pos.y, r)

    obsSpaces ++ puSpaces ++ soldierSpaces


  // --- SINGULAR SPAWN FUNCTIONS (Generate and append exactly 1 entity) ---

  /**
   * Spawns a single random obstacle (Circle or Polygon) avoiding collisions.
   * Uses tail recursion to retry upon overlap detection.
   *
   * @param g The current GameState.
   * @param border The implicit bounding box of the map.
   * @return The updated GameState containing the new obstacle.
   */
  @tailrec
  def spawnObstacle(g: GameState)(using border: BoundingBox): GameState =
    val pos = RandomGenerator.randomPosition(border.x0, border.x1, border.y0, border.y1)
    val isCircle = math.random() > 0.5
    val maxRadius = if isCircle then 0.5 + math.random() else 3.0 + math.random()

    if PrologMapChecker.hasOverlap(pos.x, pos.y, maxRadius, getOccupiedSpaces(g)) then
      spawnObstacle(g) // Collision detected, retry
    else
      val newObstacle = if isCircle then Obstacle(pos, maxRadius)
      else
        val numVertices = 3 + (math.random() * 4).toInt
        val vertices = (1 to numVertices).map: _ =>
          RandomGenerator.randomPosition(pos.x - maxRadius, pos.x + maxRadius, pos.y - maxRadius, pos.y + maxRadius)
        Obstacle(pos, vertices)

      GameState.addObstacle(g, newObstacle)


  /**
   * Spawns a single random power-up in a free space.
   *
   * @param g The current GameState.
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

    val radius = pu.shape match
      case ModelCircle(_, r) => r
      case _ => 0.2

    if PrologMapChecker.hasOverlap(pX, pY, radius, getOccupiedSpaces(g)) then
      spawnPowerUp(g)
    else
      GameState.addPowerUp(g, pu)


  /**
   * Spawns a single soldier for a specific team within the designated map segment.
   *
   * @param g The current GameState.
   * @param playerName The name of the player owning this soldier.
   * @param direction The facing direction of the soldier (1 for right, -1 for left).
   * @param spawnMinX The minimum X boundary for this specific team's spawn area.
   * @param spawnMaxX The maximum X boundary for this specific team's spawn area.
   * @param border The implicit bounding box of the map.
   * @return The updated GameState containing the newly assigned soldier.
   */
  @tailrec
  def spawnSoldier(g: GameState, playerName: String, direction: Int, spawnMinX: Double, spawnMaxX: Double)(using border: BoundingBox): GameState =
    val pX = spawnMinX + (spawnMaxX - spawnMinX) * math.random()
    val pY = border.y0 + (border.y1 - border.y0) * math.random()

    val teamCurrentSize = g.manager.teams.find(_.owner.name == playerName).map(_.soldiers.size).getOrElse(0)
    val newSoldier = initSoldier(s"$playerName-soldier${teamCurrentSize + 1}", Position(pX, pY), playerName, direction)

    val radius = newSoldier.shape match
      case ModelCircle(_, r) => r
      case _ => 0.15

    if PrologMapChecker.hasOverlap(pX, pY, radius, getOccupiedSpaces(g)) then
      spawnSoldier(g, playerName, direction, spawnMinX, spawnMaxX)
    else
      GameState.addSoldier(g, playerName, newSoldier)


  // --- PLURAL GENERATION FUNCTIONS (Folds over the state) ---

  /**
   * Generates a specified number of obstacles by folding over the GameState.
   *
   * @param count The amount of obstacles to generate.
   * @param initialGameState The starting GameState.
   * @return The resulting GameState populated with obstacles.
   */
  def generateObstacles(count: Int, initialGameState: GameState)(using border: BoundingBox): GameState =
    (1 to count).foldLeft(initialGameState)((acc, _) => spawnObstacle(acc))

  /**
   * Generates a specified number of power-ups by folding over the GameState.
   *
   * @param count The amount of power-ups to generate.
   * @param initialGameState The starting GameState.
   * @return The resulting GameState populated with power-ups.
   */
  def generatePowerUps(count: Int, initialGameState: GameState)(using border: BoundingBox): GameState =
    (1 to count).foldLeft(initialGameState)((acc, _) => spawnPowerUp(acc))

  /**
   * Divides the map, initializes the players, and generates their respective teams of soldiers.
   *
   * @param players A Set containing the names of the players.
   * @param soldierCount The number of soldiers per team.
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
      val direction = if isLeft then 1 else -1

      // Add the empty player shell to the TurnManager first
      val stateWithPlayer = GameState.addPlayer(stateAcc, initPlayer(playerName))

      // Fold over the required soldier count, adding them one by one to the specific player's team
      (1 to soldierCount).foldLeft(stateWithPlayer): (teamAcc, _) =>
        spawnSoldier(teamAcc, playerName, direction, spawnMinX, spawnMaxX)