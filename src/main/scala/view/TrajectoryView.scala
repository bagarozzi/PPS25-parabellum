package it.unibo.parabellum.view

import scalafx.scene.paint.Color._
import scalafx.scene.shape.Polyline

class TrajectoryView extends Polyline:

  stroke = Red
  strokeWidth = 2.0
  strokeDashArray = Seq(10.0, 10.0)
  visible = false

  /**
   * Aggiunge un singolo punto alla scia del proiettile man mano che avanza.
   */
  def addPoint(x: Double, y: Double): Unit =
    points.addAll(x, y)
    if (!visible.value) visible = true

  /**
   * Pulisce e nasconde la traiettoria (da chiamare quando il proiettile esplode/scompare).
   */
  def clearTrajectory(): Unit =
    points.clear()
    visible = false