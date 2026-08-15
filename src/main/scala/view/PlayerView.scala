package it.unibo.parabellum.view

import scalafx.scene.Group
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text

/**
 * Rappresenta graficamente un giocatore a schermo.
 */
class PlayerView(playerName: String, initialX: Double, initialY: Double) extends Group:

  private val ball = new Circle:
    radius = 20.0
    fill = Black
    stroke = White
    strokeWidth = 2.0

  private val nameLabel = new Text:
    text = playerName
    fill = White
    layoutY = -25.0

  nameLabel.layoutX = -nameLabel.boundsInLocal.value.getWidth / 2.0

  children = List(ball, nameLabel)

  translateX = initialX
  translateY = initialY

  /**
   * Aggiorna la posizione del giocatore a schermo.
   *
   * @param x Nuova coordinata X
   * @param y Nuova coordinata Y
   */
  def setPosition(x: Double, y: Double): Unit =
    translateX = x
    translateY = y

  /**
   * Aggiorna il nome del giocatore mostrato a schermo.
   *
   * @param newName Il nuovo nome da visualizzare
   */
  def setName(newName: String): Unit =
    nameLabel.text = newName
    // Ricalcola il centro poiché la larghezza del testo è cambiata
    nameLabel.layoutX = -nameLabel.boundsInLocal.value.getWidth / 2.0