package it.unibo.parabellum.view

import scalafx.scene.layout.{HBox, Region}
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.paint.Color.*
import scalafx.Includes.*
import scalafx.event.ActionEvent
import scalafx.scene.layout.Priority.Always

/**
 * Pannello di controllo in basso con gli input per il tiro.
 *
 * @param onShootLeft Funzione di callback eseguita quando si preme "Spara"
 */
class ControlPanelView(leftPlayerName: String, rightPlayerName: String, onShootLeft: String => Unit, onShootRight: String => Unit) extends HBox:

  // Stile del pannello: spaziatura interna e colore di sfondo scuro
  padding = Insets(15)
  spacing = 15
  style = "-fx-background-color: #222222;"

  private val spacer = new Region:
    hgrow = Always

  private val leftLabel = new Label(leftPlayerName):
    textFill = White
  private val leftField = new TextField:
    promptText = "sin(x + 2) * 30"
    prefWidth = 80
  private val leftButton = new Button("Shoot"):
    style = "-fx-cursor: hand; -fx-font-weight: bold;"
    onAction = (ae: ActionEvent) => {
     onShootLeft(leftField.text.value)
    }

  private val rightLabel = new Label(rightPlayerName):
    textFill = White
  private val rightField = new TextField:
    promptText = "cos(x + 3) * 20"
    prefWidth = 80
  private val rightButton = new Button("Shoot"):
    style = "-fx-cursor: hand; -fx-font-weight: bold;"
    onAction = () => onShootRight(leftField.text.value)


  children = List(leftLabel, leftField, leftButton,
    spacer,
    rightLabel, rightField, rightButton)