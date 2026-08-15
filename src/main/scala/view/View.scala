package it.unibo.parabellum.view

import it.unibo.parabellum.controller.GameState

/**
 * Interfaccia della View che l'Engine utilizzerà per aggiornare la grafica.
 */
trait View:
  def render(state: GameState): Unit