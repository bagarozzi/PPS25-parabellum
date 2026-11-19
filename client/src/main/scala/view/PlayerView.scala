package it.unibo.parabellum
package view

import scalafx.scene.Group
import scalafx.scene.paint.Color._
import javafx.scene.paint.ImagePattern // Classe nativa JavaFX per le texture
import javafx.scene.effect.ColorAdjust // Per ottenere altri colori di squadra dagli sprite esistenti
import scalafx.scene.image.Image       // Per caricare l'immagine
import scalafx.scene.shape.Circle
import scalafx.scene.text.Text
import scalafx.Includes._              // Per far comunicare ScalaFX e JavaFX


class PlayerView(playerName: String, initialX: Double, initialY: Double, shapeRadius: Double, teamIndex: Int) extends Group:

  private val sprite = PlayerView.spriteFor(teamIndex)
  private val spriteUrl = getClass.getResource(sprite.resource)
  if spriteUrl == null then
    throw new RuntimeException(s"File ${sprite.resource} not found in resources!")

  private val playerPattern = new ImagePattern(new Image(spriteUrl.toExternalForm).delegate)

  private val ball = new Circle:
    radius = shapeRadius
    fill = playerPattern

  if sprite.hue != 0.0 then
    ball.delegate.setEffect(new ColorAdjust(sprite.hue, 0.0, 0.0, 0.0))

  // the ring is a separate node, so that the hue shift of the sprite never alters its color:
  // the highlight of the player who is shooting is the same for every team
  private val ring = new Circle:
    radius = shapeRadius
    fill = Transparent
    stroke = White
    strokeWidth = 2.0

  private val nameLabel = new Text:
    text = playerName
    fill = White
    layoutY = -25.0

  nameLabel.layoutX = -nameLabel.boundsInLocal.value.getWidth / 2.0

  children = List(ball, ring, nameLabel)

  translateX = initialX
  translateY = initialY

  def setPosition(x: Double, y: Double): Unit =
    translateX = x
    translateY = y

  def setName(newName: String): Unit =
    nameLabel.text = newName
    nameLabel.layoutX = -nameLabel.boundsInLocal.value.getWidth / 2.0

  def setHighlight(isShooting: Boolean): Unit =
    ring.stroke = if isShooting then Yellow else White
    ring.strokeWidth = if isShooting then 4.0 else 2.0

object PlayerView:

  /**
   * The look of a team.
   * @param resource the image of the team's soldiers
   * @param hue the hue shift applied to the image, in the range [-1, 1] (that is, from -180 to +180 degrees)
   */
  private case class Sprite(resource: String, hue: Double)

  // the first two teams use the original blue and red sprites, the others use the same
  // sprites with a shifted hue (greenish and orangish respectively)
  private val sprites = Vector(
    Sprite("/Blue_Soldier_BG.jpeg", 0.0),
    Sprite("/Red_Soldier_BG.jpeg", 0.0),
    Sprite("/Blue_Soldier_BG.jpeg", -0.5),
    Sprite("/Red_Soldier_BG.jpeg", 0.17)
  )

  private def spriteFor(teamIndex: Int): Sprite = sprites(Math.floorMod(teamIndex, sprites.size))
