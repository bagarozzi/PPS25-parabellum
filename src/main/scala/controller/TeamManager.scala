package it.unibo.parabellum
package controller

import model.entity.{Player, PowerUp, Soldier}

case class Team(owner: Player, soldiers: Vector[Soldier], currentIndex: Int):
  def current : Soldier =
    soldiers.apply(currentIndex)

  def next(): Team =
    copy(currentIndex = (currentIndex + 1) % soldiers.size)
    
  def isEmpty: Boolean =
    soldiers.isEmpty

object Team:
  def initTeam(owner: Player, soldiers: Vector[Soldier]): Team =
    Team(owner, soldiers, 0)

case class TeamManager(
                        teams: Vector[Team],
                        currentIndex: Int
                      ):

  /**
   * Updates the state of the TurnManager, returning a new one.
   * @return a new TurnManager
   */
  /*def update(situation: Map[Player, Vector[Soldier]]): TeamManager =
    (for
      team <- teams.toSet
      newSoldiers = situation(team.owner)
      deadSoldier <- team.soldiers.diff(newSoldiers)
    yield deadSoldier)
      .foldLeft(this)((tm, s) => tm.eliminateDeadSoldier(s))*/

  def updateSoldier(soldier: Soldier)(f: Soldier => Option[Soldier]): TeamManager =
    copy(
      teams = teams.map(team =>
        team.copy(
          soldiers = team.soldiers.flatMap(s =>
            if s == soldier then f(s)
            else Some(s)
          )
        )
      ).filter(!_.isEmpty)
    )

  def updatePlayer(player: Player)(f: Player => Player): TeamManager =
    copy(
      teams = teams.map(team => team.copy(owner = if player == team.owner then f(player) else player))
    )

  def current: Soldier =
    teams(currentIndex).current

  def nextTurn: TeamManager =
    val updatedTeam =
    teams(currentIndex).next()

    copy(
      teams =
        teams.updated(currentIndex, updatedTeam),
      currentIndex =
        (currentIndex + 1) % teams.size
    )

  def winner: Option[Player] =
    if teams.size == 1 then Some(teams.filterNot(_.isEmpty).head.owner)
    else None

  def enemies: Set[Soldier] =
    teams.zipWithIndex.
      filter((_,idx) => idx != currentIndex).
      flatMap(_._1.soldiers).toSet
      
  def soldiers: Set[Soldier] =
    teams.flatMap(_.soldiers).toSet
    
  def currentPlayer: Player =
    teams(currentIndex).owner
      
object TeamManager:

  import Team.initTeam
  
  def initTurnManager(map: Map[Player, Vector[Soldier]]): TeamManager =
   TeamManager(map.map((player, soldiers) => initTeam(player, soldiers)).toVector, 0)

  def addPlayer(tm: TeamManager, player: Player): TeamManager =
    val newTeam = Team.initTeam(player, Vector.empty)
    tm.copy(teams = tm.teams :+ newTeam)
  
  def addSoldier(tm: TeamManager, playerName: String, soldier: Soldier): TeamManager =
    val updatedTeams = tm.teams.map: team =>
      if team.owner.name == playerName then
        team.copy(soldiers = team.soldiers :+ soldier)
      else
        team
    tm.copy(teams = updatedTeams)