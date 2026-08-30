package it.unibo.parabellum
package model.collision

import model.entity.{Figure, Obstacle, PowerUp, Soldier}

import controller.GameState
import model.function.reverse
import model.shape.{Circle, Difference, Shape}
import util.Position

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
 * An ImpactEvent represents consequences of an [[Impact]] on the game's state.
 */
sealed trait ImpactEvent:

    /**
     * Apply the consequence of the impact to the game's state.
     * @param g the [[GameState]] where to apply the consequence
     * @return the new [[GameState]]
     */
    def action(g: GameState): GameState

case class KillSoldier(soldier: Soldier) extends ImpactEvent:

    override def action(g: GameState): GameState = g.copy(manager = g.manager.eliminateDeadSoldier(soldier))

case class DamageObstacle(obstacle: Obstacle, hole: Shape) extends ImpactEvent:

    override def action(g: GameState): GameState = g.copy(obstacles = g.obstacles - obstacle + obstacle.addExplosion(hole))

case class DestroyProjectile() extends ImpactEvent:

    override def action(g: GameState): GameState = g.copy(manager = g.manager.nextTurn, projectile = None)

case class GainPowerUp(powerUp: PowerUp) extends ImpactEvent:

    override def action(g: GameState): GameState = g.copy(manager = g.manager.setPlayerPowerUp(g.manager.currentPlayer, Some(powerUp)))

case class Ricochet() extends ImpactEvent:

    override def action(g: GameState): GameState = g.copy(projectile = Some(g.projectile.get.swapFunction(g.projectile.get.trajectory.function.reverse())))

/**
 * An ImpactEffect is the behavior of a [[Projectile]] when it impacts
 * a [[Figure]] or the map's borders.
 */
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

/**
 * An [[Impact]] is a collision between a [[Projectile]] and something else.
 */
sealed trait Impact

case class FigureImpact(
                         pos: Position,
                         figure: Figure
                       ) extends Impact

case class BorderImpact() extends Impact


//GameState(gs.manager, gs.obstacles - obs + Obstacle(obs.pos, Difference(obs.shape, Set(Circle(impact.pos, 0.5)))), None, gs.pendingFunction)
//(gs: GameState) => GameState(gs.manager.eliminateDeadSoldier(sld), gs.obstacles, gs.projectiles, gs.pendingFunction)