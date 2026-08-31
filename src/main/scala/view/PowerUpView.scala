package it.unibo.parabellum.view

import scalafx.scene.Group
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Circle
import scalafx.scene.text.{Font, FontWeight, Text}

/**
 * Visual representation of a PowerUp on the screen.
 *
 * @param powerUpType The class name of the PowerUp (e.g., "Ricochet", "Burden")
 * @param initialX Initial screen X coordinate
 * @param initialY Initial screen Y coordinate
 * @param screenRadius The transformed radius for the screen
 */
class PowerUpView(powerUpType: String, initialX: Double, initialY: Double, screenRadius: Double) extends Group:

  private val (puColor, puLabel) = powerUpType match
    case "Ricochet" => (Orange, "R")
    case "Burden"   => (LightSeaGreen, "B")
    case "Random"   => (Magenta, "?")
    case "Piercing" => (Cyan, "P")
    case _          => (Yellow, "*")

  private val background = new Circle:
    radius = screenRadius
    fill = puColor
    stroke = MediumSlateBlue
    strokeWidth = 1.5

  private val textIcon = new Text:
    text = puLabel
    fill = MediumSlateBlue
    font = Font.font("Arial", FontWeight.Bold, screenRadius * 1.2)

  textIcon.layoutX = -textIcon.boundsInLocal.value.getWidth / 2.0
  textIcon.layoutY = textIcon.boundsInLocal.value.getHeight / 4.0

  children = List(background, textIcon)

  translateX = initialX
  translateY = initialY

  /**
   * Updates the position of the PowerUp on the screen.
   */
  def setPosition(x: Double, y: Double): Unit =
    translateX = x
    translateY = y