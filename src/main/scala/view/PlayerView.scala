package it.unibo.parabellum.view

import scalafx.scene.Group
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text

/**
 * Rappresenta graficamente un giocatore a schermo.
 * Estende "Group" in modo da poter raggruppare più forme insieme.
 */
class PlayerView(playerName: String, initialX: Double, initialY: Double) extends Group {

  // 1. Creiamo la palla nera
  private val ball = new Circle {
    radius = 20.0
    fill = Black
    // NOTA: Se tieni lo sfondo nero nella MainGUI, la palla non si vedrà!
    // Aggiungo un contorno bianco per farla risaltare.
    stroke = White
    strokeWidth = 2.0
  }

  // 2. Creiamo il testo col nome
  private val nameLabel = new Text {
    text = playerName
    fill = White
    // Spostiamo il testo in alto rispetto alla pallina (che ha raggio 20)
    layoutY = -25.0
  }
  // Centriamo il testo orizzontalmente calcolando la sua larghezza in automatico
  nameLabel.layoutX = -nameLabel.boundsInLocal.value.getWidth / 2.0

  // 3. Aggiungiamo gli elementi al nostro Gruppo
  children = List(ball, nameLabel)

  // 4. Posizioniamo l'intero Gruppo alle coordinate richieste
  translateX = initialX
  translateY = initialY

  /**
   * Metodo per aggiornare la posizione in futuro,
   * spostando palla e testo contemporaneamente.
   */
  def setPosition(x: Double, y: Double): Unit = {
    translateX = x
    translateY = y
  }
}