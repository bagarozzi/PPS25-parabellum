package it.unibo.parabellum
package model.collision

import model.entity.{Figure, Obstacle, Soldier}

import it.unibo.parabellum.controller.GameState
import it.unibo.parabellum.model.shape.{Circle, Difference}
import it.unibo.parabellum.util.Position

trait ImpactEffect:
  def applyEffect(impact: Impact, gs: GameState): ImpactEvent
  
trait ImpactEvent:
  def resolve(gameState: GameState): GameState

object ImpactEffect:
  def normalImpactEffect(): ImpactEffect =
    (impact: Impact, gs: GameState) => impact.hit match 
      case obs: Obstacle => (gs: GameState) => GameState(gs.manager, gs.obstacles - obs + Obstacle(obs.pos, Difference(obs.shape, Set(Circle(impact.pos, 0.5)))), None, gs.pendingFunction)
      case sld: Soldier => ???

case class Impact(
                 val pos: Position,
                 val hit: Figure
                 )

