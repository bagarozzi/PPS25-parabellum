package it.unibo.parabellum
package model.collision

import model.entity.{Figure, Obstacle, PowerUp, Soldier}

import it.unibo.parabellum.controller.GameState
import it.unibo.parabellum.model.shape.{Circle, Difference, Shape}
import it.unibo.parabellum.util.Position 

trait ImpactEffect:
  def applyEffect(impact: Impact): Set[ImpactEvent]

sealed trait ImpactEvent

case class KillSoldier(soldier: Soldier) extends ImpactEvent

case class DamageObstacle(obstacle: Obstacle, hole: Shape) extends ImpactEvent

case class DestroyProjectile() extends ImpactEvent

case class GainPowerUp(powerUp: PowerUp) extends ImpactEvent

case class Ricochet() extends ImpactEvent

object ImpactEffect:
  def normalImpactEffect(): ImpactEffect = {
      case FigureImpact(pos, obs: Obstacle) => Set(DamageObstacle(obs, Circle(pos, 0.5)), DestroyProjectile())
      case FigureImpact(pos, sld: Soldier) => Set(KillSoldier(sld))
      case FigureImpact(_, powerUp: PowerUp) => Set(GainPowerUp(powerUp))
      case BorderImpact() => Set(DestroyProjectile())
      case FigureImpact(Position(_, _), _) => Set()
    }

  def ricochetImpactEffect(): ImpactEffect = {
      case FigureImpact(pos, obs: Obstacle) => Set(DamageObstacle(obs, Circle(pos, 0.5)), DestroyProjectile())
      case FigureImpact(pos, sld: Soldier) => Set(KillSoldier(sld))
      case FigureImpact(_, powerUp: PowerUp) => Set(GainPowerUp(powerUp))
      case BorderImpact() => Set(Ricochet())
      case FigureImpact(Position(_, _), _) => Set()
    }
  
  def piercingImpactEffect(): ImpactEffect = {
    case FigureImpact(pos, obs: Obstacle) => Set(DamageObstacle(obs, Circle(pos, 0.1)))
    case FigureImpact(pos, sld: Soldier) => Set(KillSoldier(sld))
    case FigureImpact(_, powerUp: PowerUp) => Set(GainPowerUp(powerUp))
    case BorderImpact() => Set(DestroyProjectile())
    case FigureImpact(Position(_, _), _) => Set()
  }


sealed trait Impact

case class FigureImpact(
                         pos: Position,
                         figure: Figure
                       ) extends Impact

case class BorderImpact() extends Impact


//GameState(gs.manager, gs.obstacles - obs + Obstacle(obs.pos, Difference(obs.shape, Set(Circle(impact.pos, 0.5)))), None, gs.pendingFunction)
//(gs: GameState) => GameState(gs.manager.eliminateDeadSoldier(sld), gs.obstacles, gs.projectiles, gs.pendingFunction)