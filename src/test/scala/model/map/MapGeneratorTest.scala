package it.unibo.parabellum.model.map

import it.unibo.parabellum.model.entity.Player
import it.unibo.parabellum.model.shape.{Circle, Polygon}
import it.unibo.parabellum.util.MapGenerator
import org.scalatest.funsuite.AnyFunSuite

class MapGeneratorTest extends AnyFunSuite:

  val minX = -20.0
  val maxX = 20.0
  val minY = 0.0
  val maxY = 15.0

  test("generateObstacles should create exactly the requested number of obstacles"):
    val count = 10
    val obstacles = MapGenerator.generateObstacles(count, minX, maxX, minY, maxY)
    assert(obstacles.size == count)

  test("generateObstacles should place the center of all obstacles within the specified boundaries"):
    val count = 50
    val obstacles = MapGenerator.generateObstacles(count, minX, maxX, minY, maxY)

    obstacles.foreach: obs =>
      assert(obs.pos.x >= minX && obs.pos.x <= maxX, s"X position ${obs.pos.x} is out of bounds")
      assert(obs.pos.y >= minY && obs.pos.y <= maxY, s"Y position ${obs.pos.y} is out of bounds")

  test("generateObstacles should generate a mix of Shapes given a large enough count"):
    val count = 100
    val obstacles = MapGenerator.generateObstacles(count, minX, maxX, minY, maxY)

    val hasCircles = obstacles.exists(_.shape.isInstanceOf[Circle])
    val hasPolygons = obstacles.exists(_.shape.isInstanceOf[Polygon])

    assert(hasCircles)
    assert(hasPolygons)

  test("generatePlayers should create exactly two players with correct names"):
    val players = MapGenerator.generatePlayers(minX, maxX, minY, maxY, Set("player1", "player2"), 1)
    assert(players.size == 2)

    val names = players.keys.map(_.name).toSet
    assert(names.contains("player1"))
    assert(names.contains("player2"))

  test("generatePlayers should place players on opposite sides with a safe margin and within Y bounds"):
    val players = MapGenerator.generatePlayers(minX, maxX, minY, maxY, Set("player1", "player2"), 1)

    val p1 = players.keys.find(_.name == "player1").get
    val p2 = players.keys.find(_.name == "player2").get
    val s1 = players(p1).head
    val s2 = players(p2).head
    val midX = (minX + maxX) / 2.0
    val safeMargin = 2.0

    assert(s1.pos.x >= minX && s1.pos.x <= (midX - safeMargin))
    assert(s1.pos.y >= minY && s1.pos.y <= maxY)

    assert(s2.pos.x >= (midX + safeMargin) && s2.pos.x <= maxX)
    assert(s2.pos.y >= minY && s2.pos.y <= maxY)