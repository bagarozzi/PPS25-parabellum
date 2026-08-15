package it.unibo.parabellum
package controller

import view.View

trait Controller:

    def startGame(): Unit

    def addBullet(): Unit

    def updateView(g: GameState)(using view: View): Unit

object GameController extends Controller:

    private var gameState: Option[GameState] = None

    def startGame(): Unit =
        given targetFPS: Int = 60
        gameState = Some(GameState.init())
        Engine.run(gameState.get)

    def addBullet(): Unit = ???

    def updateView(g: GameState)(using view: View): Unit =
        view.render(g)