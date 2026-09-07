package it.unibo.parabellum
package view

import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.geometry.Pos
import scalafx.scene.image.{Image, ImageView}

/**
 * Represents the Main Menu scene of the game.
 * It provides input fields to customize player names and the number of soldiers per team.
 *
 * @param onPlayClicked Callback function executed when the "Play" button is clicked.
 *                      It provides Player 1's name, Player 2's name, and the number of soldiers.
 */
class MenuView(onPlayClicked: (String, String, Int) => Unit, onShootingRangeClicked: () => Unit) extends StackPane:
  private val backgroundUrl = getClass.getResource("/GameMenu.jpeg")
  if backgroundUrl == null then
    throw new RuntimeException("File /GameMenu.jpeg not found in resources")

  private val backgroundImage = new Image(backgroundUrl.toExternalForm)

  private val backgroundView = new ImageView(backgroundImage):
    fitWidth <== MenuView.this.width
    fitHeight <== MenuView.this.height
    preserveRatio = false

  private val labelStyle = "-fx-text-fill: white; -fx-font-size: 20pt; -fx-font-weight: bold;"

  private val p1Label = new Label("Player 1 Name:"):
    style = labelStyle

  private val p1Input = new TextField:
    text = "Player 1"
    maxWidth = 200

  private val p2Label = new Label("Player 2 Name:"):
    style = labelStyle

  private val p2Input = new TextField:
    text = "Player 2"
    maxWidth = 200

  private val soldiersLabel = new Label("Number of Soldiers per Player:"):
    style = labelStyle

  private val soldiersInput = new TextField:
    text = "1"
    maxWidth = 200

  private val playButton = new Button("Play"):
    style = "-fx-font-size: 20pt; -fx-font-weight: bold; -fx-padding: 10 40 10 40; -fx-cursor: hand;"
    onAction = _ => {
      val name1 = if p1Input.text.value.trim.isEmpty then "Player 1" else p1Input.text.value.trim
      val name2 = if p2Input.text.value.trim.isEmpty then "Player 2" else p2Input.text.value.trim
      val soldiersCount = soldiersInput.text.value.toIntOption.getOrElse(1)

      onPlayClicked(name1, name2, soldiersCount)
    }

  private val shootingRangeButton = new Button("Shooting range"):
    style = "-fx-font-size: 20pt; -fx-font-weight: bold; -fx-padding: 10 40 10 40; -fx-cursor: hand;"
    onAction = _ => onShootingRangeClicked()

  private val menuLayout = new VBox:
    alignment = Pos.Center
    spacing = 15
    children = List(
      p1Label, p1Input,
      p2Label, p2Input,
      soldiersLabel, soldiersInput,
      playButton,
      shootingRangeButton
    )

  children = List(backgroundView, menuLayout)