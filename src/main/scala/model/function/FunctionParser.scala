package it.unibo.parabellum
package model.function

import fastparse._
import MultiLineWhitespace._

object FunctionParser:

    /**
     * Parses a string containing a mathematical function or arithmetic equation, eventually
     *  returning it as a [[Function]]
     * @param input the string to parse
     * @return an [[Either]] containing a [[Function]] or a [[RuntimeException]]
     */
    def parse(input: String): Either[RuntimeException, Function] = fastparse.parse(input, p => expr(using p)) match
        case Parsed.Success(func, _) => Right(func)
        case failure: Parsed.Failure => Left(new IllegalArgumentException(s"Parse error: ${failure.trace().longMsg}"))

    private def number[$: P]: P[Function] = P(CharIn("+\\-").? ~ CharIn("0-9").rep(1) ~ CharIn(".").? ~ CharIn("0-9").rep(1).?).!.map(d => Function(x => d.toDouble))

    private def variable[$: P]: P[Function] = P(CharIn("+\\-").? ~ "x").!.map(s => if (s.contains("-")) -1 else 1).map(i => Function(x => i*x))

    private def parens[$: P]: P[Function] = P("(" ~/ addSub() ~ ")")

    private def sine[$: P]: P[Function] = P("sin" ~ "(" ~ addSub() ~ ")").map(func => Function(x => math.sin(func(x))))

    private def cosine[$: P]: P[Function] = P("cos" ~ "(" ~ addSub() ~ ")").map(func => Function(x => math.cos(func(x))))

    private def log[$: P]: P[Function] = P("log" ~ "(" ~ addSub() ~ ")").map(func => Function(x => math.log(func(x))))

    private def abs[$: P]: P[Function] = P("abs" ~ "(" ~ addSub() ~ ")").map(func => Function(x => math.abs(func(x))))

    private def elemFunc[$: P]: P[Function] = P(sine | cosine | log | abs)

    private def factor[$: P]: P[Function] = P(number | variable | parens | elemFunc)

    private def power[$: P]: P[Function] = P(factor ~ (CharIn("^").rep(1) ~ factor).?).map((_, _) match
        case (baseFunc, Some(exp)) => Function(x => math.pow(baseFunc(x), exp(x)))
        case (baseFunc, None) => baseFunc
    )

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