package it.unibo.parabellum
package model.entity

import util.Position
import model.shape.{Shape, Circle, Polygon, Difference}

/**
 * Rappresenta un ostacolo fisico all'interno del gioco.
 */
trait Obstacle extends Figure:
  def addExplosion(s: Shape): Obstacle

private class ObstacleImpl(val pos: Position, val shape: Shape) extends Obstacle:
  override def belongs(p: Position): Boolean = shape.belongs(p)
  override def addExplosion(s: Shape): Obstacle = shape match
    case Difference(a, b) => ObstacleImpl(pos, Difference(a, b + s))
    case _ => ObstacleImpl(pos, Difference(shape, Set(s)))

object Obstacle:


  def apply(pos: Position, radius: Double): Obstacle =
    ObstacleImpl(pos, Circle(pos, radius))
  
  def apply(pos: Position, vertices: Seq[Position]): Obstacle =
    ObstacleImpl(pos, Polygon.create(vertices))
    
  def apply(pos: Position, radius: Double, sides: Int): Obstacle =
    ObstacleImpl(pos, Polygon.regular(pos: Position, radius: Double, sides: Int))
    
  def apply(pos: Position, shape: Shape): Obstacle =
    ObstacleImpl(pos, shape)