package it.unibo.parabellum
package model.function

import util.Position

enum Direction:
  case Negative
  case Positive

  def apply(): Double = this match
    case Negative => -1
    case Positive => 1

case class Trajectory(
                          currentPosition: Position,
                          private val startingPosition: Position,
                          private val f: Function,
                          private val speed: Double,
                          private val distance: Double,
                          private val direction: Direction)

object Trajectory:

  private val INITIAL_SPEED: Double = 0.01

  def create(startPosition: Position, function: Function, direction: Direction): Trajectory = Trajectory(
    startPosition,
    startPosition,
    function,
    INITIAL_SPEED,
    startPosition.x,
    direction
  )

  extension (t: Trajectory)

    def update(dt: Double): Trajectory = Trajectory(
      newPosition(advance(dt)),
      t.startingPosition,
      t.f,
      adjustSpeed(dt),
      advance(dt),
      t.direction
    )

    private def advance(dt: Double): Double = t.distance + t.speed * dt * t.direction()

    private def adjustSpeed(h: Double): Double = t.f.derivative(t.currentPosition.x, newPosition(h).x)

    private def newPosition(x: Double): Position = Position(x, t.f(x)).traslate(offset(t.f, t.startingPosition))

  private def offset(f: Function, pos: Position): Position = Position(0, -f(pos.x))

          