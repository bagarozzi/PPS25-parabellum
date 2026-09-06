package it.unibo.parabellum
package view

import scalafx.Includes.*
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox

class EndGameView(onClick: () => Unit, height: Double, width: Double, winner: String) extends VBox:

  spacing = 20
  alignment = Pos.Center
  prefWidth = width
  prefHeight = height
  
  private val button = new Button("Back to Menu")
  button.onAction = _ => onClick()

  children = Seq(
    new Label(s"Winner: $winner"),
    button
  )