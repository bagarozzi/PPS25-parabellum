package it.unibo.parabellum
package util

/**
 * Represents a 2D position or a 2D vector and it's operations.
 * @param x the first coordinate
 * @param y the second coordinate
 */
case class Position(x: Double, y: Double):

    def traslate(x: Double, y: Double): Position = Position(this.x + x, this.y + y)

    def traslate(pos: Position): Position = traslate(pos.x, pos.y)

    def mul(a: Double, b: Double): Position = Position(this.x * a, this.y * b)
    
    def map(f: Double => Double): Position = Position(f(x), f(x))