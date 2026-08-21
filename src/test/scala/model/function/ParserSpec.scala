package it.unibo.parabellum
package model.function

import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec

class ParserSpec extends AnyFlatSpec:

    "A Parser" should "evaluate a simple arithmetic expression" in {
        val func: Function = FunctionParser.parse("2.1 + 3 + 5")
        assert(func(2) === 10.1)
    }

    "A parser" should "evaluate a simple linear function" in {
        val func: Function = FunctionParser.parse("2 * x + 1")
        assert(func(2) === 5)
    }

    "A parser" should "parse a function with the variable raised to some power" in {
        val func: Function = FunctionParser.parse("2 + x ^ 2")
        assert(func(3) === 11)
    }
    
    