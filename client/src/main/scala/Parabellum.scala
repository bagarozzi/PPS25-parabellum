package it.unibo.parabellum

import session.{LocalSession, RemoteSession}
import view.MainGUI

object Parabellum:

  // the matches of the menu are played on this machine, the online ones on a server
  private val gui = MainGUI(1000, 600, LocalSession, RemoteSession)

  @main def main(): Unit =
    gui.main(Array.empty[String])
