package it.unibo.parabellum
package controller

import view.View

import it.unibo.parabellum.model.function.Projectile
import scalafx.animation.AnimationTimer

trait Controller:

    def startGame(): Unit

    def addProjectile(angularCoefficient: Double): Unit

    def updateView(g: GameState)(using view: View): Unit

object GameController extends Controller:

    private var gameState: Option[GameState] = None
    private var gameLoop: Option[AnimationTimer] = None
    private var lastTime: Long = System.nanoTime()

    import Parabellum.given

    def startGame(): Unit =
        //given targetFPS: Int = 60
        gameState = Some(GameState.init())

        lastTime = System.nanoTime()
        val timer = AnimationTimer {
            time =>
                gameState = Some(GameState.update(gameState.get, (time - lastTime)/1_000_000))
                lastTime = time
                updateView(gameState.get)
        }
        timer.start()
        gameLoop = Some(timer)
    //Engine.run(gameState.get)

    def addProjectile(angularCoefficient: Double): Unit = gameState match
        case Some(g) => GameState.addProjectile(Projectile.parseStraightLine(g.currentTurn.pos, angularCoefficient))
        case None =>


    def updateView(g: GameState)(using view: View): Unit =
        view.render(g)