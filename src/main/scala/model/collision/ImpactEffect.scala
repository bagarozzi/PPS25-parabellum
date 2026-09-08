package it.unibo.parabellum
package model.collision

import model.entity.{Obstacle, PowerUp, Soldier}
import model.shape.Circle
import util.Position

import BorderImpactType.{HorizontalBorderImpact, VerticalBorderImpact}


/**
 * An ImpactEffect is the behavior of a [[Projectile]] when it impacts
 * a [[Figure]] or the map's borders.
 * An ImpactEffect dictates which and what [[ImpactEvent]]s are produced
 * upon an [[Impact]]
 */
trait ImpactEffect:

    /**
     * Apply the effect of this [[ImpactEffect]] producing a set of [[ImpactEvent]]s
     * @param impact the impact to react to
     * @return Some [[ImpactEvent]]s, consequence of the impact
     */
  def applyEffect(impact: Impact): Set[ImpactEvent]
    
/**
 * An ImpactEffect is the behavior of a [[Projectile]] when it impacts
 * a [[Figure]] or the map's borders.
 */
object ImpactEffect:
  def normalImpactEffect(): ImpactEffect = {
      case FigureImpact(pos, obs: Obstacle) => Set(DamageObstacle(obs, Circle(pos, 0.5)), DestroyProjectile())
      case FigureImpact(pos, sld: Soldier) => Set(KillSoldier(sld))
      case FigureImpact(_, powerUp: PowerUp) => Set(GainPowerUp(powerUp))
      case BorderImpact(_) => Set(DestroyProjectile())
      case FigureImpact(Position(_, _), _) => Set()
    }

  def ricochetImpactEffect(): ImpactEffect = {
      case FigureImpact(pos, obs: Obstacle) => Set(DamageObstacle(obs, Circle(pos, 0.5)), DestroyProjectile())
      case FigureImpact(pos, sld: Soldier) => Set(KillSoldier(sld))
      case FigureImpact(_, powerUp: PowerUp) => Set(GainPowerUp(powerUp))
      case BorderImpact(VerticalBorderImpact) => Set(DestroyProjectile())
      case BorderImpact(HorizontalBorderImpact) => Set(Ricochet())
      case FigureImpact(Position(_, _), _) => Set()
    }
  
  def piercingImpactEffect(): ImpactEffect = {
    case FigureImpact(pos, obs: Obstacle) => Set(DamageObstacle(obs, Circle(pos, 0.1)))
    case FigureImpact(pos, sld: Soldier) => Set(KillSoldier(sld))
    case FigureImpact(_, powerUp: PowerUp) => Set(GainPowerUp(powerUp))
    case BorderImpact(_) => Set(DestroyProjectile())
    case FigureImpact(Position(_, _), _) => Set()
  }

  def shootingRangeImpactEffect(): ImpactEffect = {
      case FigureImpact(pos, obs: Obstacle) => Set(DestroyObstacle(obs), SpawnNewObstacle())
      case FigureImpact(_, powerUp: PowerUp) => Set(GainPowerUp(powerUp))
      case i => normalImpactEffect().applyEffect(i)
  }

