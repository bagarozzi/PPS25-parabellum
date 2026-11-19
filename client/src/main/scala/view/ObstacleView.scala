package it.unibo.parabellum
package view

import scalafx.scene.Group
import scalafx.scene.paint.Color._
import javafx.scene.paint.ImagePattern 
import scalafx.scene.image.Image
import scalafx.scene.shape.{Circle, Polygon}
import scalafx.Includes._
/**
 * Visual presentation of an Obstacle on the map.
 */
class ObstacleView extends Group:

  private val textureUrl = getClass.getResource("/Trench_blurred.jpeg")
  if textureUrl == null then
    throw new RuntimeException("File /Trench_blurred.jpeg not found in resources!")

  private val textureImage = new Image(textureUrl.toExternalForm)

  private val obstaclePattern = new ImagePattern(textureImage)

  def drawCircle(cx: Double, cy: Double, rad: Double): Unit =
    val circleShape = new Circle:
      this.centerX = cx
      this.centerY = cy
      this.radius = rad
      fill = obstaclePattern
      stroke = Black
      strokeWidth = 2.0

    children = List(circleShape)

  def drawPolygon(vertices: Seq[(Double, Double)]): Unit =
    val shapePolygon = new Polygon:
      fill = obstaclePattern
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