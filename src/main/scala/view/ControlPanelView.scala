package it.unibo.parabellum.view

import scalafx.scene.layout.HBox
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.paint.Color.*
import scalafx.Includes.*
import scalafx.event.ActionEvent

/**
 * Bottom control panel providing the input area for the active player to shoot.
 * @param onShoot Callback function executed when the "Shoot" button is pressed,
 *                passing the user's mathematical function input.
 */
class ControlPanelView(onShoot: String => Unit) extends HBox:

  padding = Insets(15)
  spacing = 20
  style = "-fx-background-color: #222222;"
  alignment = Pos.Center

  private val turnLabel = new Label("Waiting for game..."):
    textFill = White
    style = "-fx-font-size: 16px; -fx-font-weight: bold;"

  private val inputField = new TextField:
    promptText = "e.g. sin(x + 2) * 30"
    prefWidth = 300
    style = "-fx-font-size: 14px;"

  private val shootButton = new Button("Shoot"):
    style = "-fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 14px;"
    onAction = (ae: ActionEvent) => {
      onShoot(inputField.text.value)
      // Opzionale: svuota il campo dopo aver sparato
      inputField.clear()
    }

  children = List(turnLabel, inputField, shootButton)

  /**
   * Updates the UI to show which player is currently playing.
   * @param playerName The name of the active player.
   */
  def updateCurrentPlayer(playerName: String): Unit =
    turnLabel.text = s"$playerName's turn:"