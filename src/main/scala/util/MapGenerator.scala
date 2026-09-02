package it.unibo.parabellum
package util

import model.entity.{Obstacle, Player, Soldier, PowerUp, Ricochet, Burden, Random, Piercing}
import model.entity.Player.initPlayer
import util.{Position, RandomGenerator}
import model.entity.Soldier.*
import scala.annotation.tailrec
import model.shape.{Circle => ModelCircle}

/**
 * Utility object responsible for generating the game map layout procedurally.
 * It provides functional methods to randomly spawn players, obstacles, and power-ups
 * within defined boundaries while strictly preventing overlaps via Prolog integration.
 */
object MapGenerator:

  /**
   * A higher-order generic function that abstracts the core logic of spawning entities,
   * checking for spatial overlaps, and retrying upon collisions.
   * It ensures immutability by accumulating data recursively.
   *
   * @tparam T The type of the entity being generated (e.g., Obstacle, Soldier, PowerUp).
   * @param count The total number of entities to generate.
   * @param initialData A sequence of tuples representing the already occupied spatial coordinates `(X, Y, Radius)`.
   * @param generator A lambda function that takes the current generation index and returns a tuple
   *                  containing the instantiated entity `T` and its spatial footprint `(X, Y, Radius)`.
   * @return A tuple containing a `Vector` of the successfully generated entities and the updated spatial data sequence.
   */
  private def generateEntities[T](
                                   count: Int,
                                   initialData: Seq[(Double, Double, Double)]
                                 )(generator: Int => (T, Double, Double, Double)): (Vector[T], Seq[(Double, Double, Double)]) =
    @tailrec
    def loop(index: Int, items: Vector[T], data: Seq[(Double, Double, Double)]): (Vector[T], Seq[(Double, Double, Double)]) =
      if index > count then (items, data)
      else
        val (item, x, y, r) = generator(index)
        if PrologMapChecker.hasOverlap(x, y, r, data) then
          loop(index, items, data) // Overlap detected: retry maintaining the same index
        else
          loop(index + 1, items :+ item, data :+ (x, y, r)) // Free space: accumulate and proceed

    loop(1, Vector.empty, initialData)


  /**
   * Generates a set of obstacles (a randomized mix of Circles and Polygons) within the specified area.
   *
   * @param count The total number of obstacles to spawn.
   * @param border The implicit bounding box defining the map's coordinate limits.
   * @return A tuple containing a Set of the newly generated Obstacles and the updated spatial data sequence.
   */
  def generateObstacles(count: Int)(using border: BoundingBox): (Set[Obstacle], Seq[(Double, Double, Double)]) =
    val (obstacles, data) = generateEntities(count, Seq.empty): _ =>
      val pos = RandomGenerator.randomPosition(border.x0, border.x1, border.y0, border.y1)
      val isCircle = math.random() > 0.5
      val maxRadius = if isCircle then 0.5 + math.random() else 3.0 + math.random()

      val obstacle = if isCircle then
        Obstacle(pos, maxRadius)
      else
        val numVertices = 3 + (math.random() * 4).toInt
        val vertices = (1 to numVertices).map: _ =>
          RandomGenerator.randomPosition(pos.x - maxRadius, pos.x + maxRadius, pos.y - maxRadius, pos.y + maxRadius)
        Obstacle(pos, vertices)

      (obstacle, pos.x, pos.y, maxRadius)

    (obstacles.toSet, data)


  /**
   * Generates two players and their respective soldiers, placing the teams on opposite sides
   * of the map to ensure fair gameplay. Respects the previously generated obstacles' footprints.
   *
   * @param players A Set containing the names of the players to initialize.
   * @param soldierCount The number of soldiers to generate for each team.
   * @param initialData The spatial data of already placed entities (usually obstacles) to avoid collisions.
   * @param border The implicit bounding box defining the map's coordinate limits.
   * @return A tuple containing a Map linking each Player to their Vector of Soldiers, and the updated spatial data.
   */
  def generatePlayers(players: Set[String], soldierCount: Int, initialData: Seq[(Double, Double, Double)])(using border: BoundingBox): (Map[Player, Vector[Soldier]], Seq[(Double, Double, Double)]) =
    val midX = (border.x0 + border.x1) / 2.0
    val safeMargin = 2.0

    players.toList.zipWithIndex.foldLeft((Map.empty[Player, Vector[Soldier]], initialData)):
      case ((mapAcc, currentData), (playerName, index)) =>
        val player = initPlayer(playerName)
        val isLeft = index == 0
        val spawnMinX = if isLeft then border.x0 else midX + safeMargin
        val spawnMaxX = if isLeft then midX - safeMargin else border.x1
        val direction = if isLeft then 1 else -1

        val (team, newData) = generateEntities(soldierCount, currentData): soldierIndex =>
          val pX = spawnMinX + (spawnMaxX - spawnMinX) * math.random()
          val pY = border.y0 + (border.y1 - border.y0) * math.random()
          val soldier = initSoldier(s"$playerName-soldier$soldierIndex", Position(pX, pY), playerName, direction)

          val radius = soldier.shape match
            case ModelCircle(_, r) => r
            case _ => 0.5

          (soldier, pX, pY, radius)

        (mapAcc + (player -> team), newData)


  /**
   * Randomly spawns game power-ups across the map, ensuring they do not intersect
   * with any existing obstacles or soldiers.
   *
   * @param count The number of power-ups to distribute.
   * @param initialData The accumulated spatial data of obstacles and soldiers.
   * @param border The implicit bounding box defining the map's coordinate limits.
   * @return A tuple containing a Set of the generated PowerUps and the final spatial data sequence.
   */
  def generatePowerUps(count: Int, initialData: Seq[(Double, Double, Double)])(using border: BoundingBox): (Set[PowerUp], Seq[(Double, Double, Double)]) =
    val (powerUps, data) = generateEntities(count, initialData): _ =>
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

      (pu, pX, pY, radius)

    (powerUps.toSet, data)