package it.unibo.parabellum.view

import scalafx.animation.PauseTransition
import scalafx.beans.property.{ReadOnlyDoubleProperty, ReadOnlyProperty}
import scalafx.beans.value.ObservableValue
import scalafx.scene.layout.Pane
import scalafx.scene.Node
import scalafx.scene.control.Label
import scalafx.scene.paint.Color.{Black, White}
import scalafx.scene.shape.Line
import scalafx.util.Duration

/**
 * Contenitore principale per tutti gli elementi grafici del livello di gioco.
 * Interfaccia tra la logica di gioco e la visualizzazione a schermo.
 */
class GameView(width: Double, height: Double) extends Pane:
  style = "-fx-background-color: darkgray;"
  // 1. Forza le dimensioni preferite di questo pannello
  prefWidth = width
  prefHeight = height

  private val errorTimer = new PauseTransition(Duration(6000))
  errorTimer.onFinished = _ => {
    errorLabel.visible = false
    errorLabel.text = ""
  }

  // 2. Crea gli assi cartesiani come campi privati
  private val xAxis = new Line:
    startX = 0
    startY = height / 2
    endX = width
    endY = height / 2
    stroke = Black
    strokeWidth = 1.0
    opacity = 0.5

  private val yAxis = new Line:
    startX = width / 2
    startY = 0
    endX = width / 2
    endY = height
    stroke = Black
    strokeWidth = 1.0
    opacity = 0.5


  private val errorLabel: Label = new Label:
    textFill = White
    style = """
    -fx-background-color: rgba(0, 0, 0, 0.7);
    -fx-font-family: 'monospace';
    -fx-text-alignment: left;
    -fx-font-weight: bold;
    -fx-padding: 10px;
    -fx-background-radius: 8px;
    """
    visible = false
//    layoutX <== (prefWidth - errorLabel.width) / 2
//    layoutY <== prefHeight - errorLabel.height - 30

  children.addAll(xAxis, yAxis, errorLabel)
  /**
   * Aggiunge uno o più elementi grafici al livello.
   *
   * @param nodes Elementi grafici da aggiungere (Player, Obstacle, Projectile, ecc.)
   */
  def addElements(nodes: Node*): Unit =
  //estrarre l'oggetto JavaFX originale (usando la proprietà .delegate che ogni oggetto ScalaFX possiede)
    children.addAll(nodes.map(_.delegate))

  /**
   * Rimuove un elemento grafico specifico dal livello.
   *
   * @param node L'elemento grafico da rimuovere
   */
  def removeElement(node: Node): Unit =
    children.remove(node.delegate)

  /**
   * Pulisce l'intera scena rimuovendo tutti gli elementi.
   */
  def clear(): Unit =
    children.clear()

  def showTemporaryError(message: String): Unit = {
    errorLabel.text = message
    errorLabel.visible = true

    errorTimer.playFromStart()
  }