package it.unibo.parabellum
package model.function

import fastparse._
import MultiLineWhitespace._

object FunctionParser:

    def parse(input: String): Function = fastparse.parse(input, p => expr(using p)) match
        case Parsed.Success(func, _) => func
        case failure: Parsed.Failure => throw new IllegalArgumentException(s"Parse error: ${failure.trace().longMsg}")

    private def number[$: P]: P[Function] = P(CharIn("+\\-").? ~ CharIn("0-9").rep(1) ~ CharIn(".").? ~ CharIn("0-9").rep(1).?).!.map(d => Function(x => d.toDouble))

    private def variable[$: P]: P[Function] = P("x").map(_ => Function(x => x))

    private def power[$: P]: P[Function] = P(factor ~ (CharIn("^").rep(1) ~ factor).?).map((_, _) match
        case (baseFunc, Some(exp)) => Function(x => math.pow(baseFunc(x), exp(x)))
        case (baseFunc, None) => baseFunc
    )

    private def parens[$: P]: P[Function] = P("(" ~/ addSub() ~ ")")

    private def factor[$: P]: P[Function] = P(number | variable | parens)

    private def divMul[$: P]: P[Function] = P(power ~ (CharIn("*/").! ~/ power).rep).map(eval)

    private def addSub[$: P](): P[Function] = P(divMul ~ (CharIn("+\\-").! ~/ divMul).rep).map(eval)

    private def expr[$: P]: P[Function] = P(addSub() ~ End)

    private def eval(tree: (Function, Seq[(String, Function)])): Function =
        val (base, ops) = tree
        ops.foldLeft(base) { case (leftFunc, (op, rightFunc)) => op match {
            case "+" => Function(x => leftFunc(x) + rightFunc(x))
            case "-" => Function(x => leftFunc(x) - rightFunc(x))
            case "*" => Function(x => leftFunc(x) * rightFunc(x))
            case "/" => Function(x => leftFunc(x) / rightFunc(x))
        }
        }