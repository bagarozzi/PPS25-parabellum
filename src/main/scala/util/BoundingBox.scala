package it.unibo.parabellum
package util

case class BoundingBox(x0: Double, x1: Double, y0: Double, y1: Double):
    def hsize: Double = Math.abs(x0) + Math.abs(x1)
    def vsize: Double = Math.abs(y0) + Math.abs(y1)
    def checkBoundary(pos: Position): Boolean = pos.x >= x0 && pos.x <= x1 && pos.y >= y0 && pos.y <= y1
    def ratio: Double = hsize / vsize
