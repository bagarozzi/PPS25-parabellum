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

  /**
   * Applies a transformation to a soldier of this team.
   * When the soldier is removed, the index of the next soldier to play is kept consistent:
   * it keeps pointing to the same soldier or, if that one has been removed, to the one that follows it.
   * @param soldier the soldier to update
   * @param f the transformation: `Some(s)` replaces the soldier, `None` removes it
   * @return a new team with the updated soldier, or this team if it does not contain the soldier
   */
  def updateSoldier(soldier: Soldier)(f: Soldier => Option[Soldier]): Team =
    soldiers.indexOf(soldier) match
      case -1 => this
      case idx =>
        f(soldiers(idx)) match
          case Some(updated) => copy(soldiers = soldiers.updated(idx, updated))
          case None => removeAt(idx)

  private def removeAt(idx: Int): Team =
    val remaining = soldiers.patch(idx, Nil, 1)
    val newIndex =
      if remaining.isEmpty then 0
      else if idx < currentIndex then currentIndex - 1
      else currentIndex % remaining.size
    copy(soldiers = remaining, currentIndex = newIndex)

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
    withTeams(teams.map(_.updateSoldier(soldier)(f)))

  /**
   * Replaces the teams of this manager, dropping the empty ones and keeping the turn on the
   * same team or, if that team has been eliminated, on the team that follows it.
   */
  private def withTeams(updatedTeams: Vector[Team]): TeamManager =
    val survivors = updatedTeams.filterNot(_.isEmpty)
    val newIndex =
      if survivors.isEmpty then 0
      else updatedTeams.take(currentIndex).count(!_.isEmpty) % survivors.size
    copy(teams = survivors, currentIndex = newIndex)

  /**
   * @param player the Player that has to be updated
   * @param f the transformation that modify the Player
   * @return a new TeamManager with the updated player
   */
  def updatePlayer(player: Player)(f: Player => Player): TeamManager =
    copy(
      teams = teams.map(team => team.copy(owner = if player == team.owner then f(player) else team.owner))
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

  /**
   *
   * @return the player that own the current soldier
   */
  def getAllPlayers: Set[Player] =
    teams.map(_.owner).toSet
      
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