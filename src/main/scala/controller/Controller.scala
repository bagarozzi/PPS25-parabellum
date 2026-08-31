package it.unibo.parabellum
package controller

import view.View

import it.unibo.parabellum.model.function.{Projectile, Trajectory}
import it.unibo.parabellum.util.BoundingBox
import scalafx.animation.AnimationTimer

trait Controller:

    def startGame(players: Set[String], soldiers: Int): Unit

    def addProjectile(newFunction: String): Unit

    def updateView(g: GameState)(using view: View, border: BoundingBox): Unit

object GameController extends Controller:

    private var gameState: Option[GameState] = None
    private var gameLoop: Option[AnimationTimer] = None
    private var lastTime: Long = System.nanoTime()
    private var pendingFunction: Option[String] = None

    import Parabellum.given
    
    given border: BoundingBox = BoundingBox(-25, 25, -15, 15)

    def startGame(players: Set[String], soldiers: Int): Unit =
        //given targetFPS: Int = 60
        gameState = Some(GameState.init(players,soldiers))

        lastTime = System.nanoTime()
        val timer = AnimationTimer {
            time =>
                gameState = Some(GameState.update(gameState.get, (time - lastTime)/1_000_000, pendingFunction))
                lastTime = time
                updateView(gameState.get)
                pendingFunction = None
        }
        timer.start()
        gameLoop = Some(timer)
    //Engine.run(gameState.get)

    def addProjectile(newFunction: String): Unit =
        pendingFunction = Some(newFunction)


    def updateView(g: GameState)(using view: View, border: BoundingBox): Unit =
        view.render(g)