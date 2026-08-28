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

  private val BASE_SPEED: Double = 0.007

  /**
   * Creates a new [[Trajectory]] from a starting position and a function governing
   * its movement.
   * @param startPosition the position where the trajectory starts
   * @param function the function used to calculate the next positions
   * @param direction the direction where the next position has to be calculated
   * @return a new [[Trajectory]]
   */
  def create(startPosition: Position, function: Function, direction: Direction): Trajectory = Trajectory(
    startPosition,
    startPosition,
    function,
    INITIAL_SPEED,
    startPosition.x,
    direction
  )

  extension (t: Trajectory)

    /**
     * Returns a new [[Trajectory]] with an updated position and eventually a different
     * speed depending on the function's derivative.
     * @param dt the time passed since the last update (in milliseconds)
     * @return a new updated [[Trajectory]]
     */
    def update(dt: Double): Trajectory = Trajectory(
      newPosition(t.f, advance(dt, t.distance, adjustSpeed(t.f, t.currentPosition, dt).getOrElse(INITIAL_SPEED), t.direction), t.startingPosition),
      t.startingPosition,
      t.f,
      adjustSpeed(t.f, t.currentPosition, dt).getOrElse(INITIAL_SPEED),
      advance(dt, t.distance, t.speed, t.direction),
      t.direction
    )

  private def advance(dt: Double, distance: Double, speed: Double, direction: Direction): Double = distance + speed * dt * direction()

  private def adjustSpeed(f: Function, pos: Position, h: Double): Option[Double] = Some(BASE_SPEED + INITIAL_SPEED / math.sqrt(1 + math.abs(f.derivative(pos.x)) * math.abs(f.derivative(pos.x))))

  private def newPosition(f: Function, x: Double, s: Position): Position = Position(x, f(x)).traslate(offset(f, s))

  private def offset(f: Function, pos: Position): Position = Position(0, -f(pos.x) + pos.y)

          