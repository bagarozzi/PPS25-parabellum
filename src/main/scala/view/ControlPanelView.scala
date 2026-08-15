package it.unibo.parabellum.view

import scalafx.scene.layout.HBox
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.paint.Color._
import scalafx.Includes._

/**
 * Pannello di controllo in basso con gli input per il tiro.
 *
 * @param onSpara Funzione di callback eseguita quando si preme "Spara"
 */
class ControlPanelView(onSpara: (String) => Unit) extends HBox:

  // Stile del pannello: spaziatura interna e colore di sfondo scuro
  padding = Insets(15)
  spacing = 15
  style = "-fx-background-color: #222222;"

  // --- Elementi Pendenza ---
  private val pendenzaLabel = new Label("Pendenza:"):
    textFill = White
  private val pendenzaInput = new TextField:
    promptText = "es. 45"
    prefWidth = 80

  // --- Bottone Spara ---
  private val sparaButton = new Button("Spara"):
    style = "-fx-cursor: hand; -fx-font-weight: bold;"
    onAction = _ => onSpara(pendenzaInput.text())

  children = List(pendenzaLabel, pendenzaInput, sparaButton)