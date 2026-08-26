package it.unibo.parabellum.view

import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.geometry.Pos
import scalafx.Includes.*

/**
 * Represents the Main Menu scene of the game.
 * It provides input fields to customize player names and the number of soldiers per team.
 *
 * @param onPlayClicked Callback function executed when the "Play" button is clicked.
 *                      It provides Player 1's name, Player 2's name, and the number of soldiers.
 */
class MenuView(onPlayClicked: (String, String, Int) => Unit) extends StackPane:

  private val p1Label = new Label("Player 1 Name:")
  private val p1Input = new TextField:
    text = "Player 1"
    maxWidth = 200

  private val p2Label = new Label("Player 2 Name:")
  private val p2Input = new TextField:
    text = "Player 2"
    maxWidth = 200

  private val soldiersLabel = new Label("Number of Soldiers per Player:")
  private val soldiersInput = new TextField:
    text = "1"
    maxWidth = 200

  private val playButton = new Button("Play"):
    style = "-fx-font-size: 24pt; -fx-padding: 10 40 10 40; -fx-cursor: hand;"
    onAction = _ => {
      // Clean inputs and provide fallback default values
      val name1 = if p1Input.text.value.trim.isEmpty then "Player 1" else p1Input.text.value.trim
      val name2 = if p2Input.text.value.trim.isEmpty then "Player 2" else p2Input.text.value.trim
      val soldiersCount = soldiersInput.text.value.toIntOption.getOrElse(1)

      onPlayClicked(name1, name2, soldiersCount)
    }

  private val menuLayout = new VBox:
    alignment = Pos.Center
    spacing = 15
    children = List(
      p1Label, p1Input,
      p2Label, p2Input,
      soldiersLabel, soldiersInput,
      playButton
    )

  children = menuLayout