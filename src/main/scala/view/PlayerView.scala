package it.unibo.parabellum
package view

import scalafx.scene.Group
import scalafx.scene.paint.Color._
import javafx.scene.paint.ImagePattern // Classe nativa JavaFX per le texture
import scalafx.scene.image.Image       // Per caricare l'immagine
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text
import scalafx.Includes._              // Per far comunicare ScalaFX e JavaFX


class PlayerView(playerName: String, initialX: Double, initialY: Double, shapeRadius: Double, teamIndex: Int) extends Group:

  private val t1Url = getClass.getResource("/Blue_Soldier_BG.jpeg")
  private val t2Url = getClass.getResource("/Red_Soldier_BG.jpeg")
  if t1Url == null || t2Url == null then
    throw new RuntimeException("File not found in resources!")

  private val t1Image = new Image(t1Url.toExternalForm)
  private val t2Image = new Image(t2Url.toExternalForm)

  private val playerPattern = new ImagePattern(
    if teamIndex == 0 then t1Image.delegate else t2Image.delegate
  )

  private val ball = new Circle:
    radius = shapeRadius
    fill = playerPattern 
    stroke = White
    strokeWidth = 2.0

  private val nameLabel = new Text:
    text = playerName
    fill = White
    layoutY = -25.0

  nameLabel.layoutX = -nameLabel.boundsInLocal.value.getWidth / 2.0

  children = List(ball, nameLabel)

  translateX = initialX
  translateY = initialY

  def setPosition(x: Double, y: Double): Unit =
    translateX = x
    translateY = y

  def setName(newName: String): Unit =
    nameLabel.text = newName
    nameLabel.layoutX = -nameLabel.boundsInLocal.value.getWidth / 2.0

  def setHighlight(isShooting: Boolean): Unit =
    ball.stroke = if isShooting then Yellow else White
    ball.strokeWidth = if isShooting then 4.0 else 2.0