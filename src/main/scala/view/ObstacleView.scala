package it.unibo.parabellum.view

import scalafx.scene.Group
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{Circle, Polygon, Shape}

/**
 * Visual presentation of an Obstacle on the map.
 */
class ObstacleView extends Group:

  def drawCircle(cx: Double, cy: Double, rad: Double): Unit =
    val circleShape = new Circle:
      this.centerX = cx
      this.centerY = cy
      this.radius = rad
      fill = SaddleBrown
      stroke = Black
      strokeWidth = 2.0

    children = List(circleShape)

  def drawPolygon(vertices: Seq[(Double, Double)]): Unit =
    val shapePolygon = new Polygon:
      fill = SaddleBrown
      stroke = Black
      strokeWidth = 2.0

    val flatVertices = vertices.flatMap((x, y) => Seq(x, y))
      .map(d => d.asInstanceOf[java.lang.Double])

    shapePolygon.points ++= flatVertices

    children = List(shapePolygon)

  def addHole(cx: Double, cy: Double, rad: Double): Unit =
      val hole = new HoleView()
      hole.drawHole(cx, cy, rad)
      this.children.add(hole)