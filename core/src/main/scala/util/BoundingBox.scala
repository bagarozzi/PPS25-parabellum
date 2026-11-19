package it.unibo.parabellum
package util

/**
 * BoundingBox represents a rectangular area, defined by
 * the minimum and maximum values that its points coordinates'
 * have.
 * @param x0 the lower limit on the x-coordinate
 * @param x1 the upper limit on the x-coordinate
 * @param y0 the lower limit on the y-coordinate
 * @param y1 the upper limit on the y-coordinate
 */
case class BoundingBox(x0: Double, x1: Double, y0: Double, y1: Double):

    /**
     * Returns the horizontal size of this BoundingBox.
     */
    def hsize: Double = Math.abs(x0) + Math.abs(x1)

    /**
     * Returns the vertical size of this BoundingBox.
     */
    def vsize: Double = Math.abs(y0) + Math.abs(y1)
    private def checkBoundary(pos: Position): Boolean = pos.x >= x0 && pos.x <= x1 && pos.y >= y0 && pos.y <= y1

    /**
     * Checks whether the passed position violates the x-coordinate limits of this BoundingBox
     * @param pos the position to check
     */
    def checkSideViolation(pos: Position): Boolean = !checkBoundary(pos) && (pos.x <= x0 || pos.x >= x1)

    /**
     * Checks whether the passed position violates the y-coordinate limits of this BoundingBox
     *
     * @param pos the position to check
     */
    def checkTopBottomViolation(pos: Position): Boolean = !checkBoundary(pos) && (pos.y <= y0 || pos.y >= y1)

    /**
     * Returns the aspect-ratio of this BoundingBox, as a [[Double]]
     */
    def ratio: Double = hsize / vsize
