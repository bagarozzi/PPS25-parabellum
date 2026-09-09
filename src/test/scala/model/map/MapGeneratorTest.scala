package it.unibo.parabellum
package model.map

import controller.GameController.given 
import controller.{GameState, TeamManager}
import model.shape.{Circle, Polygon}
import util.MapGenerator
import org.scalatest.funsuite.AnyFunSuite

class MapGeneratorTest extends AnyFunSuite:
  
  val gs: GameState = GameState(TeamManager(Vector.empty, 0), Set.empty, Set.empty, None, None)
    .map(MapGenerator.generateObstacles(5, _))
    .map(MapGenerator.generatePlayers(Set("player1", "player2"), 1, _))
    .map(MapGenerator.generatePowerUps(3, _))

  test("generateObstacles should create exactly the requested number of obstacles"):
    val count = 5
    assert(gs.obstacles.size == count)

  test("generateObstacles should place the center of all obstacles within the specified boundaries"):
    val count = 50
    val obstacles = gs.obstacles

    obstacles.foreach: obs =>
      assert(obs.pos.x >= border.x0 && obs.pos.x <= border.x1, s"X position ${obs.pos.x} is out of bounds")
      assert(obs.pos.y >= border.y0 && obs.pos.y <= border.y1, s"Y position ${obs.pos.y} is out of bounds")

  test("generateObstacles should generate a mix of Shapes given a large enough count"):
    val count = 100
    val obstacles = gs.obstacles
    val hasCircles = obstacles.exists(_.shape.isInstanceOf[Circle])
    val hasPolygons = obstacles.exists(_.shape.isInstanceOf[Polygon])

    assert(hasCircles)
    assert(hasPolygons)

  test("generatePlayers should create exactly two players with correct names"):
    val players = gs.manager.getAllPlayers
    assert(players.size == 2)

    val names = players.map(_.name)
    assert(names.contains("player1"))
    assert(names.contains("player2"))

  test("generatePlayers should place players on opposite sides with a safe margin and within Y bounds"):
    val soldiers = gs.manager.soldiers

    val s1 = soldiers.find(_.name == "player1-soldier1").get
    val s2 = soldiers.find(_.name == "player2-soldier1").get
    val midX = (border.x0 + border.x1) / 2.0
    val safeMargin = 2.0

    assert(s1.pos.x >= border.x0 && s1.pos.x <= (midX - safeMargin))
    assert(s1.pos.y >= border.y0 && s1.pos.y <= border.y1)

    assert(s2.pos.x >= (midX + safeMargin) && s2.pos.x <= border.x1)
    assert(s2.pos.y >= border.y0 && s2.pos.y <= border.x1)