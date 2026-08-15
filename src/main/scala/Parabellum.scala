package it.unibo.parabellum

import controller.{Controller, Engine, GameController, GameState}
import view.{MainGUI, View}

object Parabellum:

  given view: View = MainGUI(1000, 600)

  @main def main(): Unit =
    view.start()