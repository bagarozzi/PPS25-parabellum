package it.unibo.parabellum
package model.entity

import util.Position
import model.shape.{Circle, Shape}

import it.unibo.parabellum.model.function.Trajectory

trait Player:
  val name: String
  def setPowerUp(powerUp: Option[PowerUp]): Player
  def getPowerUp: Option[PowerUp]

enum State:
  case alive, dead
  
class PlayerImpl(val name: String, val powerUp: Option[PowerUp]) extends Player:
  override def setPowerUp(powerUp: Option[PowerUp]): PlayerImpl =
    PlayerImpl(name, powerUp)

  override def getPowerUp: Option[PowerUp] = powerUp
    
object Player:

  private val PLAYER_RADIUS: Double = 0.15

  def initPlayer(name: String): Player = PlayerImpl(name, None)
  