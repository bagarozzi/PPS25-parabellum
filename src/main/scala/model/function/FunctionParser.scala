package it.unibo.parabellum
package model.function

import fastparse._
import MultiLineWhitespace._

class FunctionParser(function: String):

    private def number[$: P]: P[Double] = P(CharIn("+\\-").? ~ CharIn("0-9").rep(1) ~ CharIn(".").? ~ CharIn("0-9").rep(1).?).!.map(_.toDouble)

    private def parens[$: P]: P[Double] = P("(" ~/ addSub() ~ ")")

    private def factor[$: P]: P[Double] = P(number | parens)

    private def divMul[$: P]: P[Double] = P(factor ~ (CharIn("*/").! ~/ factor).rep).map(eval)

    private def addSub[$: P](): P[Double] = P(divMul ~ (CharIn("+\\-").! ~/ divMul).rep).map(eval)

    private def expr[$: P]: P[Double] = P(addSub() ~ End)

    private def eval(tree: (Double, Seq[(String, Double)])): Double =
        val (base, ops) = tree
        ops.foldLeft(base) { case (left, (op, right)) => op match {
            case "+" => left + right
            case "-" => left - right
            case "*" => left * right
            case "/" => left / right
        }
        }

    def calculate(): Double =
        parse(function, p => expr(using p)) match
            case Parsed.Success(value, _) => value
            case failure: Parsed.Failure => throw new IllegalArgumentException("Parsing error")

object FunctionParser:

    def parse(function: String): Int = ???

