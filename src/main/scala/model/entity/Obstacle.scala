package it.unibo.parabellum
package model.entity

import util.Position
import model.shape.{Shape, Circle, Polygon}

/**
 * Rappresenta un ostacolo fisico all'interno del gioco.
 */
trait Obstacle extends Figure

private class ObstacleImpl(val pos: Position, val shape: Shape) extends Obstacle:
  override def belongs(p: Position): Boolean = shape.belongs(p)

object Obstacle:


  def apply(pos: Position, radius: Double): Obstacle =
    ObstacleImpl(pos, Circle(pos, radius))
  
  def apply(pos: Position, vertices: Seq[Position]): Obstacle =
    ObstacleImpl(pos, Polygon(vertices))
    
  def apply(pos: Position, shape: Shape): Obstacle =
    ObstacleImpl(pos, shape)