package it.unibo.parabellum
package controller

import model.entity.Soldier

case class Team(soldiers: Vector[Soldier], currentIndex: Int):
  def current : Soldier =
    soldiers.apply(currentIndex)

  def next(): Team =
    copy(currentIndex = (currentIndex + 1) % soldiers.size)

  def removeDead(): Team =
    copy(
        soldiers = soldiers.filter(_.isAlive)
    )

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
    copy(teams = teams.map(t => t.removeDead()))