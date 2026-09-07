package it.unibo.parabellum
package controller

import model.entity.{Player, Soldier}

case class Team(owner: Player, soldiers: Vector[Soldier], currentIndex: Int):

  /**
   * @return the current player of the team
   */
  def current : Soldier =
    soldiers.apply(currentIndex)

  /**
   *
   * @return a new team with updated index
   */
  def next(): Team =
    copy(currentIndex = (currentIndex + 1) % soldiers.size)

  /**
    * @return true if the team is empty, false if not
   */
  def isEmpty: Boolean =
    soldiers.isEmpty

object Team:

  /**
   * @param owner the player who own the team
   * @param soldiers the soldiers of the team
   * @return a new team
   */
  def initTeam(owner: Player, soldiers: Vector[Soldier]): Team =
    Team(owner, soldiers, 0)

case class TeamManager(
                        teams: Vector[Team],
                        currentIndex: Int
                      ):
  /**
   * @param soldier the Soldier that has to be updated
   * @param f the transformation that modify the Soldier
   * @return a new TeamManager with the updated soldier
   */
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

  /**
   * @param player the Player that has to be updated
   * @param f the transformation that modify the Player
   * @return a new TeamManager with the updated player
   */
  def updatePlayer(player: Player)(f: Player => Player): TeamManager =
    copy(
      teams = teams.map(team => team.copy(owner = if player == team.owner then f(player) else player))
    )

  /**
   * @return the Soldier that can shoot
   */
  def current: Soldier =
    teams(currentIndex).current

  /**
   * @return a copy of TeamManager with updated turn
   */
  def nextTurn: TeamManager =
    val updatedTeam =
    teams(currentIndex).next()

    copy(
      teams =
        teams.updated(currentIndex, updatedTeam),
      currentIndex =
        (currentIndex + 1) % teams.size
    )

  /**
   * @return Some[player] if he is the winner None if there isn't a winner
   */
  def winner: Option[Player] =
    if teams.size == 1 then Some(teams.filterNot(_.isEmpty).head.owner)
    else None

  /**
   * @return the set of opposing soldiers relative to the current soldier
   */
  def enemies: Set[Soldier] =
    teams.zipWithIndex.
      filter((_,idx) => idx != currentIndex).
      flatMap(_._1.soldiers).toSet

  /**
   * @return all the soldiers in game
   */
  def soldiers: Set[Soldier] =
    teams.flatMap(_.soldiers).toSet

  /**
   * @return the player that own the current soldier
   */
  def currentPlayer: Player =
    teams(currentIndex).owner
      
object TeamManager:

  import Team.initTeam

  /**
   * @param map a map that contains Player as the key and a vector of Soldier as values
   * @return a new TeamManager
   */
  def initTurnManager(map: Map[Player, Vector[Soldier]]): TeamManager =
   TeamManager(map.map((player, soldiers) => initTeam(player, soldiers)).toVector, 0)

  /**
   * @param tm initial TeamManager
   * @param player the player who will be added
   * @return a new TeamManager with a new Team owned by the new Player
   */
  def addPlayer(tm: TeamManager, player: Player): TeamManager =
    val newTeam = Team.initTeam(player, Vector.empty)
    tm.copy(teams = tm.teams :+ newTeam)

  /**
   * @param tm initial TeamManager
   * @param playerName the player who owns the team to which the new soldier is to be added
   * @param soldier the new soldier who will be added to the player's team
   * @return
   */
  def addSoldier(tm: TeamManager, playerName: String, soldier: Soldier): TeamManager =
    val updatedTeams = tm.teams.map: team =>
      if team.owner.name == playerName then
        team.copy(soldiers = team.soldiers :+ soldier)
      else
        team
    tm.copy(teams = updatedTeams)