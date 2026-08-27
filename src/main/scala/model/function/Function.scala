package it.unibo.parabellum
package model.function

/**
 * A mathematical function.
 */
opaque type Function = Double => Double

object Function:

    /**
     * Create a mathematical function from the one passed as argument
     * @param f the function to create
     * @return a Function
     */
    def apply(f: Double => Double): Function = f

extension (f: Function)

    /**
     * Get the y corresponding to the x passed.
     * @param x the value
     * @return the value calculated by the function.
     */
    def apply(x: Double): Double = f(x)

    def shift(x: Double, y: Double): Function = Function(z => f(z + x) + y)