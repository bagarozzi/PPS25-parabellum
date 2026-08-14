package it.unibo.parabellum.view

import scalafx.scene.layout.Pane
import scalafx.scene.Node

/**
 * Contenitore principale per tutti gli elementi grafici del livello di gioco.
 * Interfaccia tra la logica di gioco e la visualizzazione a schermo.
 */
class GameView extends Pane:
  style = "-fx-background-color: darkgray;"
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
    children.remove(node)

  /**
   * Pulisce l'intera scena rimuovendo tutti gli elementi.
   */
  def clear(): Unit =
    children.clear()