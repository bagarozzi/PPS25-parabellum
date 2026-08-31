package it.unibo.parabellum
package util

import model.entity.{Obstacle, Player, Soldier, PowerUp, Ricochet, Burden, Random, Piercing}
import model.entity.Player.initPlayer
import util.{Position, RandomGenerator}
import model.entity.Soldier.initSoldier
import scala.annotation.tailrec

/**
 * Utility object responsible for generating the game map layout.
 * It provides methods to randomly spawn players and obstacles within defined boundaries.
 */
object MapGenerator:

  /**
   * Generates a set of obstacles (a mix of Circles and random Polygons) within the specified area.
   * Uses tail recursion to ensure immutability and prevent overlaps via Prolog integration.
   *
   * @param count The total number of obstacles to generate.
   * @param minX  The minimum X-coordinate boundary for the spawn area.
   * @param maxX  The maximum X-coordinate boundary for the spawn area.
   * @param minY  The minimum Y-coordinate boundary for the spawn area.
   * @param maxY  The maximum Y-coordinate boundary for the spawn area.
   * @return A Set containing the newly generated Obstacle entities.
   */
  def generateObstacles(count: Int, minX: Double, maxX: Double, minY: Double, maxY: Double): (Set[Obstacle], Seq[(Double, Double, Double)]) =

    @tailrec
    def generateLoop(obstacles: Set[Obstacle], existingData: Seq[(Double, Double, Double)]): (Set[Obstacle], Seq[(Double, Double, Double)]) =
      if obstacles.size >= count then
        (obstacles, existingData)
      else
        val pos = RandomGenerator.randomPosition(minX, maxX, minY, maxY)
        val isCircle = math.random() > 0.5
        val maxRadius = if isCircle then 0.5 + math.random() else 3.0 + math.random()

        if PrologMapChecker.hasOverlap(pos.x, pos.y, maxRadius, existingData) then
          generateLoop(obstacles, existingData)
        else
          val newObstacle = if isCircle then
            Obstacle(pos, maxRadius)
          else
            val numVertices = 3 + (math.random() * 4).toInt
            val windowSize = maxRadius
            val minVX = pos.x - windowSize
            val maxVX = pos.x + windowSize
            val minVY = pos.y - windowSize
            val maxVY = pos.y + windowSize

            val vertices = (1 to numVertices).map: _ =>
              RandomGenerator.randomPosition(minVX, maxVX, minVY, maxVY)

            Obstacle(pos, vertices)

          generateLoop(
            obstacles + newObstacle,
            existingData :+ (pos.x, pos.y, maxRadius)
          )

    generateLoop(Set.empty, Seq.empty)


  /**
   * Generates two players placing them on opposite sides of the map (left and right),
   * ensuring a safe margin from the center, with random Y-coordinates.
   *
   * @param minX The minimum X-coordinate boundary (left edge of the map).
   * @param maxX The maximum X-coordinate boundary (right edge of the map).
   * @param minY The minimum Y-coordinate boundary (bottom edge of the map).
   * @param maxY The maximum Y-coordinate boundary (top edge of the map).
   * @return A Map containing the Player entities and their respective Soldiers.
   */
  def generatePlayers(minX: Double, maxX: Double, minY: Double, maxY: Double, players: Set[String], soldierCount: Int, initialData: Seq[(Double, Double, Double)]): (Map[Player, Vector[Soldier]], Seq[(Double, Double, Double)]) =
    val midX = (minX + maxX) / 2.0
    val safeMargin = 2.0

    @tailrec
    def spawnTeam(teamName: String, direction: Int, minX: Double, maxX: Double, remaining: Int, teamAcc: Vector[Soldier], dataAcc: Seq[(Double, Double, Double)]): (Vector[Soldier], Seq[(Double, Double, Double)]) =
      if remaining == 0 then (teamAcc, dataAcc)
      else
        val pX = minX + (maxX - minX) * math.random()
        val pY = minY + (maxY - minY) * math.random()
        val radius = 0.5 // Raggio di ingombro del soldato

        if PrologMapChecker.hasOverlap(pX, pY, radius, dataAcc) then
          spawnTeam(teamName, direction, minX, maxX, remaining, teamAcc, dataAcc)
        else
          val newSoldier = initSoldier(s"$teamName-soldier${teamAcc.size + 1}", Position(pX, pY), teamName, direction)
          spawnTeam(teamName, direction, minX, maxX, remaining - 1, teamAcc :+ newSoldier, dataAcc :+ (pX, pY, radius))

    // Scala foldLeft permette di passare accumulare lo stato (la mappa dei team e i dati di overlap) attraverso il Set dei giocatori
    players.toList.zipWithIndex.foldLeft((Map.empty[Player, Vector[Soldier]], initialData)):
      case ((mapAcc, currentData), (playerName, index)) =>
        val player = initPlayer(playerName)
        val isLeft = index == 0
        val spawnMinX = if isLeft then minX else midX + safeMargin
        val spawnMaxX = if isLeft then midX - safeMargin else maxX
        val direction = if isLeft then 1 else -1

        val (team, newData) = spawnTeam(playerName, direction, spawnMinX, spawnMaxX, soldierCount, Vector.empty, currentData)
        (mapAcc + (player -> team), newData)

  def generatePowerUps(count: Int, minX: Double, maxX: Double, minY: Double, maxY: Double, initialData: Seq[(Double, Double, Double)]): (Set[PowerUp], Seq[(Double, Double, Double)]) =

    @tailrec
    def spawnLoop(powerUps: Set[PowerUp], dataAcc: Seq[(Double, Double, Double)]): (Set[PowerUp], Seq[(Double, Double, Double)]) =
      if powerUps.size >= count then (powerUps, dataAcc)
      else
        val pX = minX + (maxX - minX) * math.random()
        val pY = minY + (maxY - minY) * math.random()
        val radius = 0.2 // Raggio fisso stabilito nel trait PowerUp

        if PrologMapChecker.hasOverlap(pX, pY, radius, dataAcc) then
          spawnLoop(powerUps, dataAcc)
        else
          val pos = Position(pX, pY)
          val rand = math.random()
          val pu = if rand < 0.25 then Ricochet(pos)
          else if rand < 0.50 then Burden(pos)
          else if rand < 0.75 then Random(pos)
          else Piercing(pos)

          spawnLoop(powerUps + pu, dataAcc :+ (pX, pY, radius))

    spawnLoop(Set.empty, initialData)