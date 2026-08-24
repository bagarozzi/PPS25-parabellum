package it.unibo.parabellum
package model.shape

import it.unibo.parabellum.util.Position
import org.scalatest.flatspec.AnyFlatSpec

class ShapeSpec extends AnyFlatSpec:

    "A Difference" should "return the correct diffSet" in {
        val diff: Set[Shape] = Set(
            Circle(Position(1,0), 1),
            Circle(Position(10,0), 2),
            Circle(Position(0,10), 3)
            )
        val shape = Difference(Circle(Position(0,0), 1), diff)
        assert(shape.diffSet === diff)
    }

    "A Difference" should "return the correct diffSet when the diffSet has recursive differences inside" in {
        val recDiff: Set[Shape] = Set(
            Circle(Position(1, 0), 3),
            Circle(Position(11, 0), 2),
            Circle(Position(0, 10), 31)
        )
        val diff: Set[Shape] = Set(
            Circle(Position(1, 0), 1),
            Circle(Position(10, 0), 2),
            Difference(Circle(Position(0, 10), 3), recDiff)
        )
        val shape = Difference(Circle(Position(0, 0), 1), diff)
        assert(shape.diffSet === Set(
            Circle(Position(1, 0), 3),
            Circle(Position(11, 0), 2),
            Circle(Position(0, 10), 31),
            Circle(Position(1, 0), 1),
            Circle(Position(10, 0), 2),
            Circle(Position(0, 10), 3)
        ))
    }
