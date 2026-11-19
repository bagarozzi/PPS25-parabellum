package it.unibo.parabellum.view


import scalafx.scene.paint.Color._
import scalafx.scene.shape.Circle

/**
 * Visual presentation of a hole in an Obstacle.
 */
class HoleView extends Circle:

  def drawHole(cx: Double, cy: Double, rad: Double): Unit =

      this.centerX = cx
      this.centerY = cy
      this.radius = rad
      this.fill = DarkGray



