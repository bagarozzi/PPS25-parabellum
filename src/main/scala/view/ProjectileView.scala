package it.unibo.parabellum.view

import scalafx.scene.paint.Color._
import scalafx.scene.shape.Circle

/**
 * Rappresenta graficamente un proiettile a schermo.
 */
class ProjectileView(initialX: Double, initialY: Double) extends Circle {

  // Impostiamo l'aspetto visivo del proiettile
  radius = 8.0 // Lo facciamo un po' più piccolo del giocatore
  fill = Black

  // Aggiungiamo un contorno bianco sottile per farlo risaltare sul grigio scuro
  stroke = White
  strokeWidth = 1.0

  // Posizione iniziale
  centerX = initialX
  centerY = initialY

  /**
   * Aggiorna la posizione del proiettile a schermo.
   * Da chiamare a ogni "tick" dell'Engine durante il volo.
   */
  def setPosition(x: Double, y: Double): Unit = {
    centerX = x
    centerY = y
  }
}