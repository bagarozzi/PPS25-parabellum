package it.unibo.parabellum
package model.entity

import model.collision.ImpactEffect


trait Player:
  val name: String
  def setPowerUp(powerUp: Option[PowerUp]): Player
  def getPowerUp: Option[PowerUp]
  def impactEffect: ImpactEffect

enum State:
  case alive, dead
  
class PlayerImpl(val name: String, val powerUp: Option[PowerUp], val impactEffect: ImpactEffect) extends Player:
  override def setPowerUp(powerUp: Option[PowerUp]): PlayerImpl =
    PlayerImpl(name, powerUp, impactEffect)

  override def getPowerUp: Option[PowerUp] = powerUp
    
object Player:

  private val PLAYER_RADIUS: Double = 0.15

  def initPlayer(name: String, impactEffect: ImpactEffect): Player = PlayerImpl(name, None, impactEffect)
  