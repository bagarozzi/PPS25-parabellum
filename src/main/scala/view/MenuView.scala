package it.unibo.parabellum.view

import scalafx.scene.layout.StackPane
import scalafx.scene.control.Button
import scalafx.Includes._

/**
 * Rappresenta la scena del Menu Principale.
 *
 * @param onPlayClicked Funzione di callback eseguita al click del pulsante "Play".
 */
class MenuView(onPlayClicked: () => Unit) extends StackPane:

  private val playButton = new Button("Play"):
    style = "-fx-font-size: 24pt; -fx-padding: 10 40 10 40; -fx-cursor: hand;"
    onAction = _ => onPlayClicked()

  children = playButton
