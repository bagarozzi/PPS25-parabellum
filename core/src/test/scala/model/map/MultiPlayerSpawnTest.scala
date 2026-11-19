package it.unibo.parabellum
package model.map

import controller.{GameState, TeamManager}
import model.function.Direction
import util.{BoundingBox, MapGenerator}
import org.scalatest.funsuite.AnyFunSuite

class MultiPlayerSpawnTest extends AnyFunSuite:

  given border: BoundingBox = BoundingBox(-25, 25, -15, 15)

  private def generate(names: Seq[String], soldiers: Int): GameState =
    GameState(TeamManager(Vector.empty, 0), Set.empty, Set.empty, None, None)
      .map(MapGenerator.generateObstacles(5, _))
      .map(MapGenerator.generatePlayers(names, soldiers, _))

  private def soldiersOf(gs: GameState, player: String) =
    gs.manager.teams.find(_.owner.name == player).get.soldiers

  for count <- MapGenerator.MinPlayers to MapGenerator.MaxPlayers do

    test(s"generatePlayers should create $count teams with the requested soldiers, in turn order"):
      val names = (1 to count).map(i => s"p$i")
      val gs = generate(names, 2)
      assert(gs.manager.teams.map(_.owner.name) == names.toVector)
      assert(gs.manager.teams.forall(_.soldiers.size == 2))
      assert(gs.manager.soldiers.size == count * 2)

    test(s"with $count players every soldier spawns inside the sector of its team"):
      val names = (1 to count).map(i => s"p$i")
      val gs = generate(names, 3)
      names.zip(MapGenerator.sectors(count)).foreach((name, sector) =>
        assert(soldiersOf(gs, name).forall(s => sector.contains(s.pos))))

  test("two players get the left half and the right half of the map"):
    val gs = generate(Seq("p1", "p2"), 3)
    assert(soldiersOf(gs, "p1").forall(_.pos.x < 0))
    assert(soldiersOf(gs, "p2").forall(_.pos.x > 0))

  test("the direction proposed to a soldier is the horizontal one towards the center of the map"):
    val gs = generate(Seq("p1", "p2", "p3"), 2)
    gs.manager.soldiers.foreach: s =>
      val expected = if s.pos.x < 0 then Direction.Positive else Direction.Negative
      assert(s.facingDirection == expected)

  test("all the soldiers have a different name"):
    val gs = generate(Seq("p1", "p2", "p3", "p4"), 3)
    assert(gs.manager.soldiers.map(_.name).size == 12)

  test("generatePlayers should reject more players than the map supports"):
    assertThrows[IllegalArgumentException](generate((1 to MapGenerator.MaxPlayers + 1).map(i => s"p$i"), 1))

  test("generatePlayers should reject duplicated player names"):
    assertThrows[IllegalArgumentException](generate(Seq("same", "same"), 1))
