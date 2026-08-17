package it.unibo.parabellum
package view

import util.{BoundingBox, Position}

case class WindowSize(width: Double, height: Double)

/**
 * Converts Cartesian coordinates to coordinates usable by the view.
 */
object GeometryHelper:
    /**
     * Transforms Cartesian coordinates coming from the Engine to coordinates
     * usable by the View.
     * @param pos the Cartesian coordinates
     * @param windowSize the size of the window
     * @param boundingBox the size of the logic-side bounding box
     * @return the coordinates in the View's reference system
     */
    def transform(pos: Position)(using windowSize: WindowSize, boundingBox: BoundingBox): Position =
        val rx: Double = windowSize.width/boundingBox.hsize
        val ry: Double = -windowSize.height/boundingBox.vsize
        Position(pos.x * rx, pos.y * ry).traslate(windowSize.width/2, windowSize.height/2)