package it.unibo.parabellum
package controller

import view.View
import model.function.{Function, FunctionParser, ParsingError}

import model.entity.Player
import util.BoundingBox
import scalafx.animation.AnimationTimer

trait Controller:

    def startGame(players: Set[String], soldiers: Int): Unit

    def addProjectile(newFunction: String): Option[ParsingError]

    def updateView(g: GameState)(using view: View, border: BoundingBox): Unit

object GameController extends Controller:

    private var gameState: Option[GameState] = None
    private var gameLoop: Option[AnimationTimer] = None
    private var lastTime: Long = System.nanoTime()
    private var pendingFunction: Option[Function] = None

    import Parabellum.given
    
    given border: BoundingBox = BoundingBox(-25, 25, -15, 15)

    def startGame(players: Set[String], soldiers: Int): Unit =
        gameState = Some(GameState.init(players, soldiers))
        startSession(g => g.winner.isDefined, g => view.showEndGame(g.winner.get.name))

    def startShootingRange(): Unit =
        gameState = Some(GameState.initShootingRange())
        startSession(_ => false, _ => ())

    private def startSession(endGamePredicate: GameState => Boolean, endGameAction: GameState => Unit): Unit =
        lastTime = System.nanoTime()
        lazy val timer: AnimationTimer = AnimationTimer {
            time =>
                gameState = Some(GameState.update(gameState.get, (time - lastTime) / 1_000_000, pendingFunction))
                if endGamePredicate(gameState.get) then
                    timer.stop()
                    endGameAction(gameState.get)
                else
                    lastTime = time
                    pendingFunction = None
                    updateView(gameState.get)
        }
        timer.start()
        gameLoop = Some(timer)
        
    def stopSession(): Unit =
        gameLoop.foreach(_.stop())


    def addProjectile(newFunction: String): Option[ParsingError] = FunctionParser.parse(newFunction) match
        case Left(err) => Some(err)
        case Right(func) =>
            pendingFunction = Some(func)
            None


    def updateView(g: GameState)(using view: View, border: BoundingBox): Unit =
        view.render(g)