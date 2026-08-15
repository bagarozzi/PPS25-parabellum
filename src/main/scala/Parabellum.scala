package it.unibo.parabellum

import controller.{Engine, GameState}
import view.MainGUI

object Parabellum:

  @main def main(): Unit =
    // Create: View, GameState and pass it to the main loop
    given targetFPS: Int = 60
    MainGUI.main(Array.empty[String])
    Engine.run(GameState.init())
