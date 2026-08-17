package it.unibo.parabellum.view

import scalafx.scene.layout.Pane
import scalafx.scene.Node
import scalafx.scene.paint.Color.Black
import scalafx.scene.shape.Line

/**
 * Contenitore principale per tutti gli elementi grafici del livello di gioco.
 * Interfaccia tra la logica di gioco e la visualizzazione a schermo.
 */
class GameView(width: Double, height: Double) extends Pane:
  style = "-fx-background-color: darkgray;"
  // 1. Forza le dimensioni preferite di questo pannello
  prefWidth = width
  prefHeight = height

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

  children.addAll(xAxis, yAxis)
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