package it.unibo.parabellum
package util

/**
 * Represents a 2D position or a 2D vector and it's operations.
 * @param x the first coordinate
 * @param y the second coordinate
 */
case class Position(x: Double, y: Double):

    /**
     * Returns a new [[Position]], translated by the
     * passed, independent, values.
     * @param x the quantity to traslate the first coordinate
     * @param y the quantity to traslate the second coordinate
     */
    def traslate(x: Double, y: Double): Position = Position(this.x + x, this.y + y)

    /**
     * Returns a new [[Position]] with both coordinates
     * translated by the passed Position (represents a *vector*).
     *
     * @param pos the vector to translate this Position
     */
    def traslate(pos: Position): Position = traslate(pos.x, pos.y)

    /**
     * Returns a new [[Position]] with both coordinates
     * scaled by the passed parameters.
     * @param a the parameter to scale the first coordinate with
     * @param b the parameter to scale the first coordinate with
     */
    def mul(a: Double, b: Double): Position = Position(this.x * a, this.y * b)

    /**
     * Applies the passed function to both coordinates, returning a new [[Position]]
     * @param f the function to apply
     */
    def map(f: Double => Double): Position = Position(f(x), f(x))