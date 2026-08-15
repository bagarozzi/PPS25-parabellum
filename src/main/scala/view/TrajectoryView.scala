package it.unibo.parabellum.view

import scalafx.scene.paint.Color._
import scalafx.scene.shape.Polyline

/**
 * Rappresenta graficamente una traiettoria curva calcolata dal Model.
 */
class TrajectoryView extends Polyline:

  stroke = Red
  strokeWidth = 2.0
  strokeDashArray = Seq(10.0, 10.0)
  visible = false

  /**
   * Disegna la curva unendo i punti calcolati dall'Engine.
   *
   * @param curvePoints Una sequenza di tuple (X, Y) che rappresentano i punti lungo la traiettoria.
   */
  def updateTrajectory(curvePoints: Seq[(Double, Double)]): Unit =
    points.clear()

    curvePoints.foreach: (x, y) =>
      points.addAll(x, y)

    visible = true

  def hideTrajectory(): Unit =
    visible = false