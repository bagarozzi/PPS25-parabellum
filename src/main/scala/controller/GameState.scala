package it.unibo.parabellum
package controller

import model.entity.{Obstacle, Player}

import it.unibo.parabellum.util.Position
import it.unibo.parabellum.model.entity.Player.initPlayer
import model.function.{Projectile, Trajectory}

import it.unibo.parabellum.model.collision.CollisionDetector.detectCollision
import it.unibo.parabellum.model
import it.unibo.parabellum.model.collision.{CollisionDetector, ImpactEffect}
import it.unibo.parabellum.model.entity.State.dead
import it.unibo.parabellum.model.shape.{Circle, Polygon}

/**
 * Represents the state of the game in a certain instant in time.
 * @param players the set of players currently in the game
 * @param projectiles the projectile that are being fired
 */
class GameState(val players: Set[Player],val obstacles: Set[Obstacle], val projectiles: Option[Projectile], val currentTurn: Player)
object GameState:
  var pendingTrajectory: Option[Trajectory] = None
  /**
   * Updates the game state, returning a new state.
   * @param g the game state to update
   * @return the new game state
   */
  def update(g: GameState, dt: Double): GameState =
    var newProjectile: Option[Projectile] = g.projectiles.map(p => p.update(dt))
    val players = g.players.diff(g.players.filter(p => p.state == dead))
    //se si vuole dividere cerchi e poligoni
    //val circles = g.obstacles.filter(o => o.shape.isInstanceOf[Circle])
    //val polygons = g.obstacles.filter(o => o.shape.isInstanceOf[Polygon])
    var newTurn: Player = g.currentTurn
    if(newProjectile.isDefined) then {
      newProjectile = detectCollision(newProjectile.get, players - g.currentTurn)
      if newProjectile.isEmpty then
        newTurn = changeTurn(players, g.currentTurn)
    }
    if(g.projectiles.isEmpty && pendingTrajectory.isDefined) then {
      newProjectile = Some(Projectile.create(g.currentTurn.pos, pendingTrajectory.get, 1))
      pendingTrajectory = None
    }
    GameState(players, g.obstacles, newProjectile, g.currentTurn)



  private def changeTurn(players: Set[Player], currentTurn: Player): Player =
    players.find(p=> p!= currentTurn).get
    
  def addProjectile(trajectory: Trajectory): Unit =
    pendingTrajectory = Some(trajectory)

  def addObstacle(g: GameState, obstacle: Obstacle): GameState =
    GameState(g.players, g.obstacles + obstacle, g.projectiles, g.currentTurn)

  def init(): GameState =
    val players = Set(initPlayer("player1", Position(-7.5, 0.0)), initPlayer("player2", Position(7.5, 0.0)))
    val obstacles = Set.empty[Obstacle]
    val circle = Obstacle(Position(5.0, 3.0), 20.0)
    GameState(
    players, obstacles+circle,
    None,
    players.head)