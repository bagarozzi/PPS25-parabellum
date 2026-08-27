package it.unibo.parabellum
package model.function

import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec

class ParserSpec extends AnyFlatSpec:

    "A Parser" should "evaluate a simple arithmetic expression" in {
        val func: Function = FunctionParser.parse("2.1 + 3 + 5") match
            case Right(func) => func
            case Left(e) => Function(x => x)
        assert(func(2) === 10.1)
    }

    "A parser" should "evaluate a simple linear function" in {
        val func: Function = FunctionParser.parse("2 * x + 1") match
            case Right(func) => func
            case Left(e) => Function(x => x)
        assert(func(2) === 5)
    }

    "A parser" should "parse a function with the variable raised to some power" in {
        val func: Function = FunctionParser.parse("2 + x ^ 2") match
            case Right(func) => func
            case Left(e) => Function(x => x)
        assert(func(3) === 11)
    }

    "A parser" should "parse a function containing parenthesis" in {
        val func: Function = FunctionParser.parse("2 + 3 * x + (x + 2) ^ 2") match
            case Right(func) => func
            case Left(e) => Function(x => x)
        assert(func(4) === 50)
    }

    "A parser" should "parse a sine function" in {
        val func: Function = FunctionParser.parse("sin(x) + 3") match
            case Right(func) => func
            case Left(e) => Function(x => x)
        assert(func(2) === (math.sin(2) + 3))
    }

    "A parser" should "parse a cosine function" in {
        val func: Function = FunctionParser.parse("cos(x) + 3") match
            case Right(func) => func
            case Left(e) => Function(x => x)
        assert(func(2) === (math.cos(2) + 3))
    }

    "A Parser" should "parse a logarithm function" in {
        val func: Function = FunctionParser.parse("1 - x * log(x)") match
            case Right(func) => func
            case Left(e) => Function(x => x)
        assert(func(5) === (1 - 5 * math.log(5)))
    }

    "A Parser" should "parse a modulus function" in {
        val func: Function = FunctionParser.parse("(abs(x + 2) * 5) * sin(x)") match
            case Right(func) => func
            case Left(e) => Function(x => x)
        assert(func(6) === (math.abs(8) * 5) * math.sin(6))
    }


