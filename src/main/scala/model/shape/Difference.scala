package it.unibo.parabellum
package model.shape

import util.Position

case class Difference(a: Shape, b: Set[Shape]) extends Shape:

  override def belongs: Position => Boolean =
    p => a.belongs(p) && !b.map(_.belongs(p)).foldLeft(false)(_||_)

  def diffSet: Set[Shape] =
    def findDiffSet(a: Shape): Set[Shape] = a match
      case Difference(c, d) => (d + c).flatMap(findDiffSet)
      case s => Set(s)
    b.flatMap(findDiffSet)
