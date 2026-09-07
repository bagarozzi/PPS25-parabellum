package it.unibo.parabellum
package controller

import view.View
import model.function.{Function, FunctionParser, ParsingError, Projectile, Trajectory}

import it.unibo.parabellum.model.entity.Player
import it.unibo.parabellum.util.BoundingBox
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
        startSession()

    def startShootingRange(): Unit =
        gameState = Some(GameState.initShootingRange())
        startSession()

    private def startSession(): Unit =
        lastTime = System.nanoTime()
        val timer = AnimationTimer {
            time =>
                gameState = Some(GameState.update(gameState.get, (time - lastTime) / 1_000_000, pendingFunction))
                val winner = gameState.flatMap(_.winner)
                winner match
                    case Some(p: Player) =>
                        gameLoop.foreach(_.stop())
                        view.showEndGame(p.name)
                        gameState = None
                    case None => updateView(gameState.get)
                lastTime = time
                pendingFunction = None
        }
        timer.start()
        gameLoop = Some(timer)


    def addProjectile(newFunction: String): Option[ParsingError] = FunctionParser.parse(newFunction) match
        case Left(err) => Some(err)
        case Right(func) =>
            pendingFunction = Some(func)
            None


    def updateView(g: GameState)(using view: View, border: BoundingBox): Unit =
        view.render(g)
        
    def showEndGame(): Unit = ???