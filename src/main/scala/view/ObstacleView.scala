package it.unibo.parabellum.view

import scalafx.scene.Group
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Polygon

/**
 * Rappresenta graficamente un ostacolo sulla mappa.
 */
class ObstacleView extends Group:

  private val shapePolygon = new Polygon:
    fill = SaddleBrown
    stroke = Black
    strokeWidth = 2.0

  children = List(shapePolygon)

  /**
   * Disegna la forma dell'ostacolo a partire dai suoi vertici.
   *
   * @param vertices Sequenza di coordinate (X, Y) che definiscono il perimetro.
   */
  def drawShape(vertices: Seq[(Double, Double)]): Unit =
    shapePolygon.points.clear()

    // In Scala 3 possiamo usare l'untupling automatico delle tuple nei parametri
    vertices.foreach: (x, y) =>
      shapePolygon.points.addAll(x, y)