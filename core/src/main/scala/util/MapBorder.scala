package it.unibo.parabellum
package util

/**
 * The bounding box of the game map, shared by every module.
 * Import it with `import util.MapBorder.given` wherever a [[BoundingBox]] is required implicitly.
 */
object MapBorder:
  given border: BoundingBox = BoundingBox(-25, 25, -15, 15)
