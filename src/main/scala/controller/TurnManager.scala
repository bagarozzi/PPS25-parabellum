package it.unibo.parabellum
package controller

import model.entity.Soldier

case class Team(soldiers: Vector[Soldier], currentIndex: Int):
  def current : Soldier =
    soldiers.apply(currentIndex)

  def next(): Team =
    copy(currentIndex = (currentIndex + 1) % soldiers.size)

  def removeDead(): Team =
    val newSoldiers =
      soldiers.filter(_.isAlive)
    copy(
    soldiers = newSoldiers,
    currentIndex =
      if newSoldiers.isEmpty then 0
      else currentIndex % newSoldiers.size
  )
    
  def isEmpty: Boolean =
    soldiers.isEmpty
  

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
    
  def eliminateDeadSoldier: TurnManager =
    val newTeams = teams.map(t => t.removeDead()).filter(t => !t.isEmpty)
    copy(newTeams)
    

  def winner: Option[Team] =
    if teams.size == 1 then Some(teams.head)
    else None