package it.unibo.parabellum
package util

import model.entity.{Obstacle, Player, Soldier}
import model.entity.Player.initPlayer
import util.{Position, RandomGenerator}
import model.entity.Soldier.initSoldier
/**
 * Utility object responsible for generating the game map layout.
 * It provides methods to randomly spawn players and obstacles within defined boundaries.
 */
object MapGenerator:
  /**
   * Generates a set of obstacles (a mix of Circles and random Polygons) within the specified area.
   * Does not currently perform overlap detection.
   *
   * @param count The total number of obstacles to generate.
   * @param minX  The minimum X-coordinate boundary for the spawn area.
   * @param maxX  The maximum X-coordinate boundary for the spawn area.
   * @param minY  The minimum Y-coordinate boundary for the spawn area.
   * @param maxY  The maximum Y-coordinate boundary for the spawn area.
   * @return A Set containing the newly generated Obstacle entities.
   */
  def generateObstacles(count: Int, minX: Double, maxX: Double, minY: Double, maxY: Double): Set[Obstacle] =
    var obstacles = Set.empty[Obstacle]

    var existingData = Seq.empty[(Double, Double, Double)]

    while obstacles.size < count do
      val pos = RandomGenerator.randomPosition(minX, maxX, minY, maxY)
      val isCircle = math.random() > 0.5

      val maxRadius = if isCircle then 0.5 + math.random() else 3.0 + math.random()

      val isOverlapping = PrologMapChecker.hasOverlap(pos.x, pos.y, maxRadius, existingData)

      if !isOverlapping then
        existingData = existingData :+ (pos.x, pos.y, maxRadius)

        if isCircle then
          obstacles = obstacles + Obstacle(pos, maxRadius)
        else
          val numVertices = 3 + (math.random() * 4).toInt
          val windowSize = maxRadius

          val minVX = pos.x - windowSize
          val maxVX = pos.x + windowSize
          val minVY = pos.y - windowSize
          val maxVY = pos.y + windowSize

          val vertices = (1 to numVertices).map: _ =>
            RandomGenerator.randomPosition(minVX, maxVX, minVY, maxVY)

          obstacles = obstacles + Obstacle(pos, vertices)

    obstacles

  /**
   * Generates two players placing them on opposite sides of the map (left and right),
   * ensuring a safe margin from the center, with random Y-coordinates.
   *
   * @param minX The minimum X-coordinate boundary (left edge of the map).
   * @param maxX The maximum X-coordinate boundary (right edge of the map).
   * @param minY The minimum Y-coordinate boundary (bottom edge of the map).
   * @param maxY The maximum Y-coordinate boundary (top edge of the map).
   * @return A Set containing the two initialized Player entities.
   */
  def generatePlayers(minX: Double, maxX: Double, minY: Double, maxY: Double, players: Set[String], soldier: Int): Map[Player, Vector[Soldier]] =

    val midX = (minX + maxX) / 2.0
    val safeMargin = 2.0

    val soldiers = for {
      (playerName, index) <- players.toList.zipWithIndex
      player = initPlayer(playerName)

      isLeft = index == 0
      minSpawnX = if isLeft then minX else midX + safeMargin
      maxSpawnX = if isLeft then midX - safeMargin else maxX
      direction = if isLeft then 1 else -1

      team = (1 to soldier).map { i =>
        val pX = minSpawnX + (maxSpawnX - minSpawnX) * math.random()
        val pY = minY + (maxY - minY) * math.random()
        initSoldier(s"$playerName-soldier$i", Position(pX, pY), playerName, direction)
      }.toVector
    } yield (player, team)

    soldiers.toMap
        
  