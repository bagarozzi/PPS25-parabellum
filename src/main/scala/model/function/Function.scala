package it.unibo.parabellum
package model.function

opaque type Function = Double => Double

object Function:
    def apply(f: Double => Double): Function = f

extension (f: Function)
    def apply(x: Double): Double = f(x)

    def shift(x: Double, y: Double): Function = Function(z => f(z + x) + y)