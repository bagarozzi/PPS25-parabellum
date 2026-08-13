package it.unibo.parabellum.view

import scalafx.scene.paint.Color._
import scalafx.scene.shape.Polyline

/**
 * Rappresenta graficamente una traiettoria curva calcolata dal Model.
 */
class TrajectoryView extends Polyline {

  // Stile della linea curva
  stroke = Red
  strokeWidth = 2.0
  strokeDashArray = Seq(10.0, 10.0)
  visible = false

  /**
   * Disegna la curva unendo i punti calcolati dall'Engine.
   *
   * @param curvePoints Una sequenza di tuple (X, Y) che rappresentano
   *                    i punti lungo la formula matematica.
   */
  def updateTrajectory(curvePoints: Seq[(Double, Double)]): Unit = {
    // 1. Svuota la linea precedente
    points.clear()

    // 2. Aggiunge le nuove coordinate X e Y alla Polyline
    curvePoints.foreach { case (x, y) =>
      points.addAll(x, y)
    }

    visible = true
  }

  def hideTrajectory(): Unit = {
    visible = false
  }
}