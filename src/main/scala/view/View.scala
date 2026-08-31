package it.unibo.parabellum.view

import it.unibo.parabellum.controller.GameState
import it.unibo.parabellum.util.BoundingBox

/**
 * Interfaccia della View che l'Engine utilizzerà per aggiornare la grafica.
 */
trait View:
  def render(state: GameState)(using border: BoundingBox): Unit
  
  def start(): Unit