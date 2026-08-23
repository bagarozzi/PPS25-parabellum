package it.unibo.parabellum.util

import scala.util.Random

object RandomGenerator:

  private val random = new Random()

  def randomPosition(minX: Double, maxX: Double, minY: Double, maxY: Double): Position =
    val x = minX + (maxX - minX) * random.nextDouble()
    val y = minY + (maxY - minY) * random.nextDouble()
    Position(x, y)