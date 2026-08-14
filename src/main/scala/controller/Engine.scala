package it.unibo.parabellum
package controller

import scala.annotation.tailrec

object Engine:

  def run(g: GameState)(using targetFPS: Int): Unit =
    val optimalTimeNs: Long = 1_000_000_000L / targetFPS
    loop(optimalTimeNs, g)

  @tailrec
  private def loop(loopTime: Long, g: GameState): Unit =
    val startTime = System.nanoTime()
    /*
      Do work here...
    */

    val endTime = System.nanoTime()
    val frameTime = endTime - startTime
    val waitTime = loopTime - frameTime

    if (waitTime > 0) {
      val waitTimeMs = waitTime / 1_000_000L
      val waitTimeNanoRem = (waitTime % 1_000_000L).toInt
      Thread.sleep(waitTimeMs, waitTimeNanoRem)
    }
    /* Exit loop when a certain condition is met */
    if(true) {
      loop(loopTime, g)
    }
