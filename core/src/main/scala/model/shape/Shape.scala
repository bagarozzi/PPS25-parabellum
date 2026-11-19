package it.unibo.parabellum
package model.shape

import util.Position

/**
 * A Shape is an enclosed area where some point's membership can be verified.
 */
trait Shape:

  /**
   * Checks whether a position belongs to the shape
   * @return
   */
  def belongs: Position => Boolean
