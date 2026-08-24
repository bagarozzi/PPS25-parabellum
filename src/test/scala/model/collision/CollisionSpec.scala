package it.unibo.parabellum
package model.collision

import it.unibo.parabellum.model.collision.ImpactEffect.normalImpactEffect
import it.unibo.parabellum.model.entity.{Obstacle, Player, PlayerImpl, Soldier}
import it.unibo.parabellum.model.function.{FunctionParser, Projectile, Trajectory}
import it.unibo.parabellum.model.shape.Circle
import it.unibo.parabellum.util.Position
import org.scalatest.flatspec.AnyFlatSpec

class CollisionSpec extends AnyFlatSpec:

    "Collisions" should "detect an out-of-bounds projectile" in {
        val projectile = Projectile.createProjectile(Position(-16, 26), "x", 0)
        assert(CollisionDetector.detectCollision(projectile, Set()) === Set(DestroyProjectile))
    }

    "Collisions" should "not detect a projectile in the bounds" in {
        val projectile = Projectile.createProjectile(Position(0, 0), "x", 0)
        assert(CollisionDetector.detectCollision(projectile, Set()).isEmpty)
    }

    "Collisions" should "detect a projectile hitting a figure" in {
        val projectile = Projectile.createProjectile(Position(0, 0), "x", 0)
        val obs = Obstacle(Position(1,0), Circle(Position(1,0), 1))
        val secondObs = Obstacle(Position(10, 10), Circle(Position(10, 10), 1))
        assert(CollisionDetector.detectCollision(projectile, Set(obs, secondObs))
            .map {
                case DamageObstacle(o, _) if o == obs => true
                case DamageObstacle(o, _) if o != obs => false
                case DestroyProjectile => true
                case _ => false
            }.forall(identity))
    }

    "Collision" should "not detect a projectile hitting nothing" in {
        val projectile = Projectile.createProjectile(Position(0, 0), "x", 0)
        val obs = Obstacle(Position(-10, -10), Circle(Position(-10, -10), 1))
        val secondObs = Obstacle(Position(10, 10), Circle(Position(10, 10), 1))
        assert(CollisionDetector.detectCollision(projectile, Set(obs, secondObs)).isEmpty)
    }

    "Collision" should "detect a projectile hitting a player with obstacles around" in {
        val projectile = Projectile.createProjectile(Position(0, 0), "x", 0)
        val obs = Obstacle(Position(10, 10), Circle(Position(10, 10), 1))
        val soldier = Soldier.initSoldier("soldier-1", Position(0, 0.15), Player.initPlayer("pippo"), 0)
        assert(CollisionDetector.detectCollision(projectile, Set(obs, soldier))
            .map {
                case KillSoldier(s) if s == soldier => true
                case _ => false
            }.forall(identity))
    }

