package it.unibo.parabellum.view

import it.unibo.parabellum.snapshot.GameSnapshot
import it.unibo.parabellum.util.BoundingBox

/**
 * Interfaccia della View che l'Engine utilizzerà per aggiornare la grafica.
 */
trait View:
  def render(snapshot: GameSnapshot)(using border: BoundingBox): Unit
  def showEndGame(winner: String): Unit

  /** Tells the user that something he did was not valid, e.g. a function that cannot be parsed. */
  def showError(message: String): Unit

  def start(): Unit