package it.unibo.parabellum
package view

import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.control.{Button, Label}
import scalafx.geometry.Pos
import scalafx.scene.image.{Image, ImageView}

class EndGameView(onClick: () => Unit, viewHeight: Double, viewWidth: Double, winner: String) extends StackPane:

  private val backgroundUrl = getClass.getResource("/Victory.jpeg")
  if backgroundUrl == null then
    throw new RuntimeException("File /Victory.jpeg not found in resources")

  private val backgroundImage = new Image(backgroundUrl.toExternalForm)

  private val backgroundView = new ImageView(backgroundImage):
    fitWidth <== EndGameView.this.width
    fitHeight <== EndGameView.this.height
    preserveRatio = false

  prefWidth = viewWidth
  prefHeight = viewHeight

  private val winnerLabel = new Label(s"Winner: $winner"):
    style = "-fx-text-fill: white; -fx-font-size: 32pt; -fx-font-weight: bold;"

  private val button = new Button("Back to Menu"):
    style = "-fx-font-size: 20pt; -fx-font-weight: bold; -fx-padding: 10 40 10 40; -fx-cursor: hand;"
    onAction = _ => onClick()

  private val uiLayout = new VBox:
    alignment = Pos.Center
    spacing = 30
    children = Seq(
      winnerLabel,
      button
    )

  children = Seq(backgroundView, uiLayout)