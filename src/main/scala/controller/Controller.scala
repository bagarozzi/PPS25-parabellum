package it.unibo.parabellum
package controller

import view.View

import it.unibo.parabellum.model.function.{Projectile, Trajectory}
import scalafx.animation.AnimationTimer

trait Controller:

    def startGame(): Unit

    def addProjectile(angularCoefficient: Double): Unit

    def updateView(g: GameState)(using view: View): Unit

object GameController extends Controller:

    private var gameState: Option[GameState] = None
    private var gameLoop: Option[AnimationTimer] = None
    private var lastTime: Long = System.nanoTime()
    private var pendingFunction: Option[String] = None

    import Parabellum.given

    def startGame(): Unit =
        //given targetFPS: Int = 60
        gameState = Some(GameState.init())

        lastTime = System.nanoTime()
        val timer = AnimationTimer {
            time =>
                gameState = Some(GameState.update(gameState.get, (time - lastTime)/1_000_000, pendingFunction))
                lastTime = time
                updateView(gameState.get)
        }
        timer.start()
        gameLoop = Some(timer)
    //Engine.run(gameState.get)

    def addProjectile(newFunction: String): Unit =
        pendingFunction = Some(newFunction)


    def updateView(g: GameState)(using view: View): Unit =
        view.render(g)