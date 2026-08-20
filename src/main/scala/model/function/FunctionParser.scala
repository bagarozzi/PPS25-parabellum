package it.unibo.parabellum
package model.function

import fastparse._
import MultiLineWhitespace._

class FunctionParser(function: String):

    private def number[$: P]: P[Int] = P(CharIn("0-9").rep(1).!.map(_.toInt))

    private def parens[$: P]: P[Int] = P("(" ~/ addSub ~ ")")

    private def factor[$: P]: P[Int] = P(number | parens)

    private def divMul[$: P]: P[Int] = P(factor ~ (CharIn("*/").! ~/ factor).rep).map(eval)

    private def addSub[$: P]: P[Int] = P(divMul ~ (CharIn("+\\-").! ~/ divMul).rep).map(eval)

    private def expr[$: P]: P[Int] = P(addSub ~ End)

    private def eval(tree: (Int, Seq[(String, Int)])): Int =
        val (base, ops) = tree
        ops.foldLeft(base) { case (left, (op, right)) => op match {
            case "+" => left + right
            case "-" => left - right
            case "*" => left * right
            case "/" => left / right
        }
        }

    def calculate(): Int =
        parse(function, p => expr(using p)) match
            case Parsed.Success(value, _) => value
            case failure: Parsed.Failure => throw new IllegalArgumentException("Parsing error")

object FunctionParser:

    def parse(function: String): Int = ???

