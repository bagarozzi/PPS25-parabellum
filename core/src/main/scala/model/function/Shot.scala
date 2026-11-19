package it.unibo.parabellum
package model.function

/**
 * A shot decided by a player: the [[Function]] that the projectile follows and the [[Direction]]
 * along the x-axis in which it travels. The direction is chosen by the player at every shot
 * and does not depend on the team.
 *
 * @param function  the function followed by the projectile
 * @param direction the direction of travel of the projectile
 */
case class Shot(function: Function, direction: Direction)
