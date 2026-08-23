package it.unibo.parabellum
package model.entity

import util.Position
import model.shape.{Circle, Shape}

import it.unibo.parabellum.model.function.Trajectory

trait Player:
  val name: String

enum State:
  case alive, dead
  
class PlayerImpl(val name: String) extends Player

  
object Player:

  private val PLAYER_RADIUS: Double = 0.15

  def initPlayer(name: String): Player = PlayerImpl(name)
