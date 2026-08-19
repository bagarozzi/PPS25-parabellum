package it.unibo.parabellum.view

import scalafx.scene.paint.Color._
import scalafx.scene.shape.Circle

/**
 * Rappresenta graficamente un proiettile a schermo.
 */
class ProjectileView(initialX: Double, initialY: Double) extends Circle:

  radius = 4.0
  fill = Black
  stroke = White
  strokeWidth = 1.0

  centerX = initialX
  centerY = initialY

  /**
   * Aggiorna la posizione del proiettile a schermo.
   *
   * @param x Nuova coordinata X
   * @param y Nuova coordinata Y
   */
  def setPosition(x: Double, y: Double): Unit =
    centerX = x
    centerY = y