package it.unibo.parabellum
package controller

import model.collision.ImpactEffect.normalImpactEffect
import model.entity.{Player, Soldier}
import model.function.Direction
import util.Position
import org.scalatest.flatspec.AnyFlatSpec

class TeamManagerSpec extends AnyFlatSpec:

  private def soldier(owner: String, n: Int): Soldier =
    Soldier.initSoldier(s"$owner-$n", Position(n, 0), owner, Direction.Positive)

  private def team(owner: String, soldiers: Int, currentIndex: Int = 0): Team =
    Team(Player.initPlayer(owner, normalImpactEffect()), (1 to soldiers).map(soldier(owner, _)).toVector, currentIndex)

  private def manager(currentIndex: Int, teams: Team*): TeamManager =
    TeamManager(teams.toVector, currentIndex)

  private def fourTeams(currentIndex: Int): TeamManager =
    manager(currentIndex, team("a", 1), team("b", 1), team("c", 1), team("d", 1))

  private def kill(tm: TeamManager, owner: String, n: Int): TeamManager =
    tm.updateSoldier(soldier(owner, n))(_ => None)

  "A TeamManager with four teams" should "give the turn to every player in order and then start over" in {
    val order = Iterator.iterate(fourTeams(0))(_.nextTurn).take(5).map(_.currentPlayer.name).toList
    assert(order == List("a", "b", "c", "d", "a"))
  }

  it should "consider the soldiers of all the other teams as enemies" in {
    assert(fourTeams(1).enemies.map(_.name) == Set("a-1", "c-1", "d-1"))
  }

  it should "alternate the soldiers of a team on its turns" in {
    val tm = manager(0, team("a", 2), team("b", 1))
    assert(tm.current.name == "a-1")
    assert(tm.nextTurn.nextTurn.current.name == "a-2")
  }

  it should "keep the turn on the same team when a team before it is eliminated" in {
    val tm = kill(fourTeams(2), "b", 1)
    assert(tm.teams.size == 3)
    assert(tm.currentPlayer.name == "c")
  }

  it should "keep the turn on the same team when a team after it is eliminated" in {
    val tm = kill(fourTeams(0), "c", 1)
    assert(tm.teams.size == 3)
    assert(tm.currentPlayer.name == "a")
  }

  it should "pass the turn to the following team when the current team is eliminated" in {
    assert(kill(fourTeams(1), "b", 1).currentPlayer.name == "c")
  }

  it should "pass the turn to the first team when the last team is eliminated" in {
    assert(kill(fourTeams(3), "d", 1).currentPlayer.name == "a")
  }

  it should "have no winner until only one team is left" in {
    val tm = manager(0, team("a", 1), team("b", 1), team("c", 1))
    assert(tm.winner.isEmpty)
    assert(kill(tm, "b", 1).winner.isEmpty)
    assert(kill(kill(tm, "b", 1), "c", 1).winner.map(_.name) == Some("a"))
  }

  it should "keep a valid turn while soldiers are being killed" in {
    val start = manager(0, team("a", 3), team("b", 3), team("c", 3), team("d", 3))
    val victims = List(("b", 1), ("c", 3), ("a", 2), ("d", 1), ("c", 1), ("b", 3))
    val end = victims.foldLeft(start): (tm, victim) =>
      val (owner, n) = victim
      val afterKill = kill(tm, owner, n)
      assert(afterKill.soldiers.contains(afterKill.current))
      afterKill.nextTurn
    assert(end.soldiers.size == 6)
  }

  "A Team" should "keep pointing to the same soldier when one that plays earlier is killed" in {
    val updated = team("a", 3, 2).updateSoldier(soldier("a", 1))(_ => None)
    assert(updated.current == soldier("a", 3))
  }

  it should "keep pointing to the same soldier when one that plays later is killed" in {
    val updated = team("a", 3, 0).updateSoldier(soldier("a", 2))(_ => None)
    assert(updated.current == soldier("a", 1))
  }

  it should "give the turn to the following soldier when the current one is killed" in {
    val updated = team("a", 3, 1).updateSoldier(soldier("a", 2))(_ => None)
    assert(updated.current == soldier("a", 3))
  }

  it should "start over from the first soldier when the current last one is killed" in {
    val updated = team("a", 3, 2).updateSoldier(soldier("a", 3))(_ => None)
    assert(updated.current == soldier("a", 1))
  }

  it should "replace a soldier without touching the turn" in {
    val moved = Soldier.initSoldier("a-2", Position(9, 9), "a", Direction.Positive)
    val updated = team("a", 3, 1).updateSoldier(soldier("a", 2))(_ => Some(moved))
    assert(updated.current == moved)
    assert(updated.soldiers.size == 3)
  }
