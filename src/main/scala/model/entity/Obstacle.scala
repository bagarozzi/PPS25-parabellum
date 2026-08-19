package it.unibo.parabellum
package model.entity

import util.Position
import model.shape.{Shape, Circle, Polygon} // Assumiamo che Polygon verrà creato in model.shape

/**
 * Rappresenta un ostacolo fisico all'interno del gioco.
 */
trait Obstacle extends Figure

private class ObstacleImpl(val pos: Position, val shape: Shape) extends Obstacle:
  override def belongs(p: Position): Boolean = shape.belongs(p)

object Obstacle:

  /**
   * Crea un ostacolo di forma circolare.
   *
   * @param pos Posizione (centro del cerchio)
   * @param radius Raggio del cerchio
   */
  def apply(pos: Position, radius: Double): Obstacle =
    ObstacleImpl(pos, Circle(pos, radius))

  /**
   * Crea un ostacolo di forma poligonale.
   *
   * @param pos Posizione di riferimento (es. centro di massa o primo vertice)
   * @param vertices Sequenza di vertici che definiscono il poligono
   */
  def apply(pos: Position, vertices: Seq[Position]): Obstacle =
    ObstacleImpl(pos, Polygon(vertices))