package it.unibo.parabellum.model.map

import it.unibo.parabellum.model.entity.{Obstacle, Player}
import it.unibo.parabellum.model.entity.Player.initPlayer
import it.unibo.parabellum.util.{Position, RandomGenerator}
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
    (1 to count).map: _ =>
      val pos = RandomGenerator.randomPosition(minX, maxX, minY, maxY)

      if math.random() > 0.5 then
        val radius = 0.5 + math.random()
        Obstacle(pos, radius)
      else
        val numVertices = 3 + (math.random() * 4).toInt
        val windowSize = 0.5 + math.random()

        val minVX = pos.x - windowSize
        val maxVX = pos.x + windowSize
        val minVY = pos.y - windowSize
        val maxVY = pos.y + windowSize

        val vertices = (1 to numVertices).map: _ =>
          RandomGenerator.randomPosition(minVX, maxVX, minVY, maxVY)

        Obstacle(pos, vertices)
    .toSet

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
  def generatePlayers(minX: Double, maxX: Double, minY: Double, maxY: Double): Set[Player] =
    val midX = (minX + maxX) / 2.0
    val safeMargin = 2.0

    val p1X = minX + (midX - safeMargin - minX) * math.random()
    val p2X = (midX + safeMargin) + (maxX - (midX + safeMargin)) * math.random()

    val p1Y = minY + (maxY - minY) * math.random()
    val p2Y = minY + (maxY - minY) * math.random()

    val pos1 = Position(p1X, p1Y)
    val pos2 = Position(p2X, p2Y)

    Set(
      initPlayer("player1", pos1),
      initPlayer("player2", pos2)
    )