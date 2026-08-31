package it.unibo.parabellum

import controller.{Controller, GameController, GameState}
import view.{MainGUI, View}

object Parabellum:

  private val gui = MainGUI(1000, 600)
  given view: View = gui

  @main def main(): Unit =
    gui.main(Array.empty[String])