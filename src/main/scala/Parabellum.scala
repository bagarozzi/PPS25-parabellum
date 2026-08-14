package it.unibo.parabellum

import controller.{Engine, GameState}

object Parabellum:

  @main def main(): Unit =
    // Create: View, GameState and pass it to the main loop
    given targetFPS: Int = 60
    Engine.run(GameState.init())
