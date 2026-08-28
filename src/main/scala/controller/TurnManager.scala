package it.unibo.parabellum
package controller

import model.entity.{Player, PowerUp, Soldier}

case class Team(owner: Player, soldiers: Vector[Soldier], currentIndex: Int):
  def current : Soldier =
    soldiers.apply(currentIndex)

  def next(): Team =
    copy(currentIndex = (currentIndex + 1) % soldiers.size)

  def removeSoldier(s: Soldier): Team =
    val newSoldiers =
      soldiers.filterNot(_== s)
    copy(
    soldiers = newSoldiers,
    currentIndex =
      if newSoldiers.isEmpty then 0
      else currentIndex % newSoldiers.size
  )
    
  def isEmpty: Boolean =
    soldiers.isEmpty
  
  def setPlayerPowerUp(powerUp: Option[PowerUp]): Team =
    copy(owner = owner.setPowerUp(powerUp))
    
object Team:
  def initTeam(owner: Player, soldiers: Vector[Soldier]): Team =
    Team(owner, soldiers, 0)
    
  

case class TurnManager(
                        teams: Vector[Team],
                        currentIndex: Int
                      ):
  
  def current: Soldier =
    teams(currentIndex).current

  def nextTurn: TurnManager =
    val updatedTeam =
    teams(currentIndex).next()

    copy(
      teams =
        teams.updated(currentIndex, updatedTeam),
      currentIndex =
        (currentIndex + 1) % teams.size
    )
    
  def eliminateDeadSoldier(s: Soldier): TurnManager =
    val newTeams = teams.map(t => t.removeSoldier(s)).filter(t => !t.isEmpty)
    copy(teams = newTeams, currentIndex = currentIndex % newTeams.size)
    

  def winner: Option[Team] =
    if teams.size == 1 then Some(teams.head)
    else None

  def enemies: Set[Soldier] =
    teams.zipWithIndex.
      filter((_,idx) => idx != currentIndex).
      flatMap(_._1.soldiers).toSet
      
  def soldiers: Set[Soldier] =
    teams.flatMap(_.soldiers).toSet

  def setPlayerPowerUp(player: Player, powerUp: Option[PowerUp]): TurnManager =
    copy(
      teams = teams.map(t => if player.name == t.owner.name then
          t.setPlayerPowerUp(powerUp)
        else t
      )
    )
    
  def currentPlayer: Player =
    teams(currentIndex).owner
      
object TurnManager:

  import Team.initTeam
  
  def initTurnManager(map: Map[Player, Vector[Soldier]]): TurnManager =
   TurnManager(map.map((player, soldiers) => initTeam(player, soldiers)).toVector, 0)