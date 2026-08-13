package it.unibo.parabellum.view

import scalafx.scene.Group
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Polygon

/**
 * Rappresenta graficamente un ostacolo sulla mappa.
 */
class ObstacleView extends Group {

  // Creiamo un poligono vuoto. Sarà il Controller a dargli la forma!
  private val shapePolygon = new Polygon {
    fill = SaddleBrown // Un colore marrone per simulare il terreno/ostacolo
    stroke = Black
    strokeWidth = 2.0
  }

  // Aggiungiamo il poligono al nostro gruppo
  children = List(shapePolygon)

  /**
   * Metodo esposto per permettere al Controller/ViewModel di disegnare la forma.
   *
   * @param vertices Una sequenza di coordinate (X, Y) che rappresentano gli spigoli.
   */
  def drawShape(vertices: Seq[(Double, Double)]): Unit = {
    // 1. Svuota eventuali forme precedenti
    shapePolygon.points.clear()

    // 2. Aggiunge i nuovi vertici per "scolpire" l'ostacolo
    vertices.foreach { case (x, y) =>
      shapePolygon.points.addAll(x, y)
    }
  }
}