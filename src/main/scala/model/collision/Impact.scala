package it.unibo.parabellum
package model.collision

import model.entity.Figure
import util.Position


/**
 * An [[Impact]] is a collision between a [[Projectile]] and something else.
 */
trait Impact

case class FigureImpact(
                         pos: Position,
                         figure: Figure
                       ) extends Impact

case class BorderImpact(b: BorderImpactType) extends Impact

enum BorderImpactType:
  case VerticalBorderImpact
  case HorizontalBorderImpact
