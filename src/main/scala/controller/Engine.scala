package it.unibo.parabellum
package controller

import it.unibo.parabellum.controller.GameState.update
import controller.GameController
import it.unibo.parabellum.view.View

import scala.annotation.tailrec

object Engine:

  import Parabellum.given

  def run(g: GameState)(using targetFPS: Int): Unit =
    val optimalTimeNs: Long = 1_000_000_000L / targetFPS
    loop(optimalTimeNs, g)

  @tailrec
  private def loop(loopTime: Long, g: GameState): Unit =
    val startTime = System.nanoTime()

    val newGameState = update(g, loopTime * 1000)
    GameController.updateView(newGameState)

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
      loop(loopTime, newGameState)
    }
