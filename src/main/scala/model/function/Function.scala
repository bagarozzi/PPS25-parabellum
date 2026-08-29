package it.unibo.parabellum
package model.function

import model.function.Function.DERIVATIVE_H

/**
 * A mathematical function.
 */
opaque type Function = Double => Double

object Function:

    val DERIVATIVE_H = 0.0001

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

    /**
     * Combines the functions
     * @param g the function to combine
     * @param op how the functions are to be combined
     * @return the combined functions
     */
    def combine(g: Function)(op: (Function, Function) => Function): Function = op(f, g)

    /**
     * Returns a function that applies first [[this]] function and the passed one.
     * f.concat(g) becomes f(g(x))
     * @param g the function to apply last
     * @return the concatenated functions
     */
    def concat(g: Function): Function = Function(x => f(g(x)))

    /**
     * Returns a function that applies first [[this]] function and the passed one.
     * f.concat(g) becomes f(g(x))
     * @param g the function to apply last
     * @return the concatenated functions
     */
    def ++(g: Function): Function = Function(x => f(g(x)))

    /**
     * Returns the sum of this and the new function passed
     * @param g the [[Function]] to sum
     * @return the sum of the functions
     */
    def +(g: Function): Function = combine(g)((f,g) => Function(x => f(x) + g(x)))

    /**
     * Returns the difference of [[this]] and the new function passed.
     * @param g the [[Function]] to subtract
     * @return a [[Function]], difference of the two functions
     */
    def -(g: Function): Function = combine(g)((f, g) => Function(x => f(x) - g(x)))

    /**
     * Returns the product of this and the new function passed
     *
     * @param g the [[Function]] to sum
     * @return the product of the functions
     */
    def *(g: Function): Function = combine(g)((f,g) => Function(x => f(x) * g(x)))

    /**
     * Returns the reversed function
     * @return the reversed [[Function]]
     */
    def reverse(): Function = Function(x => -f(x))

    /**
     * Calculate the derivative of the function in a value x and for some
     * increment h
     * @param x the value on which calculate the derivative
     * @param h the increment over the value
     * @return
     */
    def derivative(x: Double): Double = (f(x + DERIVATIVE_H) - f(x))/DERIVATIVE_H