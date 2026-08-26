package it.unibo.parabellum
package controller

import view.View

import it.unibo.parabellum.model.function.{Projectile, Trajectory}
import scalafx.animation.AnimationTimer

trait Controller:

    def startGame(player1: String, player2:String, soldiers: Int): Unit

    def addProjectile(newFunction: String): Unit

    def updateView(g: GameState)(using view: View): Unit

object GameController extends Controller:

    private var gameState: Option[GameState] = None
    private var gameLoop: Option[AnimationTimer] = None
    private var lastTime: Long = System.nanoTime()
    private var pendingFunction: Option[String] = None

    import Parabellum.given

    def startGame(player1: String, player2:String, soldiers: Int): Unit =
        //given targetFPS: Int = 60
        gameState = Some(GameState.init(player1,player2,soldiers))

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


    def updateView(g: GameState)(using view: View): Unit =
        view.render(g)