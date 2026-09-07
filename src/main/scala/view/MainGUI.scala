package it.unibo.parabellum
package view

import controller.{GameController, GameState}
import model.entity.{Obstacle, PowerUp}
import model.function.ParsingError
import model.shape.{Difference, Circle as ModelCircle, Polygon as ModelPolygon}
import util.BoundingBox
import view.TrajectoryView

import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color.*

/**
 * Main GUI of the Parabellum game.
 * It maps Model entities (players, obstacles, projectiles) into visual objects
 * and handles UI updates at each game frame.
 *
 * @param width  Window width in pixels.
 * @param height Window height in pixels.
 */
class MainGUI(width: Double, height: Double) extends JFXApp3 with View:

  given windowSize: WindowSize = WindowSize(width, height)

  private lazy val gameView = new GameView(windowSize.width, windowSize.height)
  private val trajectoryView = new TrajectoryView()
  private lazy val controlPanel = new ControlPanelView(userInput =>
    GameController.addProjectile(userInput) match
      case Some(ParsingError(message)) => showParsingError(message)
      case _ =>,
    () => endShootingMode()
  )

  private var playerViews: Map[String, PlayerView] = Map.empty
  private var projectileView: Option[ProjectileView] = None
  private var obstacleViews: Map[Obstacle, ObstacleView] = Map.empty
  private var powerUpViews: Map[PowerUp, PowerUpView] = Map.empty

  override def start(): Unit =

    gameView.addElements(trajectoryView)


    val rootPane = new BorderPane:
      center = gameView
      bottom = controlPanel

    val gameScene = new Scene:
      root = rootPane

    def avviaGioco(p1Name: String, p2Name: String, soldiers: Int): Unit = {
      GameController.startGame(Set(p1Name, p2Name), soldiers)

      stage.scene = gameScene
      stage.sizeToScene()
      stage.centerOnScreen()
    }

    val menuPane = new MenuView(avviaGioco)
    val menuScene = new Scene(windowSize.width, windowSize.height):
      fill = White
      root = menuPane

    stage = new JFXApp3.PrimaryStage:
      title = "Parabellum"
      resizable = false
      scene = menuScene

  override def showEndGame(winner: String): Unit =
    val winnerPane = new EndGameView(() => restartGame(), height, width, winner)
    val winnerScene = new Scene:
      root = winnerPane
      
    stage.scene = winnerScene

  def endShootingMode(): Unit =
    restartGame()

  private def restartGame(): Unit =
    playerViews = Map.empty
    projectileView = None
    obstacleViews = Map.empty
    powerUpViews = Map.empty
    gameView.clear()
    start()


  override def render(state: GameState)(using border: BoundingBox): Unit =
    Platform.runLater:
      controlPanel.updateCurrentPlayer(state.manager.current.name)
      state.obstacles.foreach: obs =>
        if !obstacleViews.contains(obs) then
          val view = new ObstacleView()

          obs.shape match
            case diff @ Difference(baseShape, _) =>
              baseShape match
                case ModelCircle(center, radius) =>
                  val tc = GeometryHelper.transform(center)
                  view.drawCircle(tc.x, tc.y, GeometryHelper.transform(radius))
                case ModelPolygon(vertices) =>
                  val screenVertices = vertices.map: v =>
                    val tc = GeometryHelper.transform(v)
                    (tc.x, tc.y)
                  view.drawPolygon(screenVertices)
                case _ => ()

              diff.diffSet.foreach:
                case ModelCircle(center, radius) =>
                  val tc = GeometryHelper.transform(center)
                  view.addHole(tc.x, tc.y, GeometryHelper.transform(radius))
                case _ => ()

            case ModelCircle(center, radius) =>
              val tc = GeometryHelper.transform(center)
              view.drawCircle(tc.x, tc.y, GeometryHelper.transform(radius))

            case ModelPolygon(vertices) =>
              val screenVertices = vertices.map: v =>
                val tc = GeometryHelper.transform(v)
                (tc.x, tc.y)
              view.drawPolygon(screenVertices)

          obstacleViews += (obs -> view)
          gameView.addElements(view)

      val obstaclesToRemove = obstacleViews.keys.toSet.diff(state.obstacles)
      obstaclesToRemove.foreach: obs =>
        val view = obstacleViews(obs)
        gameView.removeElement(view)
        obstacleViews -= obs


      state.manager.soldiers.foreach: soldier =>
        val tc = GeometryHelper.transform(soldier.pos)

        playerViews.get(soldier.name) match
          case Some(view) =>
            view.setPosition(tc.x, tc.y)
            val isHisTurn = soldier == state.manager.current
            view.setHighlight(isHisTurn)
          case None =>
            val ModelCircle(pos, radius) = soldier.shape.runtimeChecked   
            val newView = new PlayerView(soldier.name, tc.x, tc.y, GeometryHelper.transform(radius))
            playerViews += (soldier.name -> newView)
            gameView.addElements(newView)
      //remove players
      val currentSoldiers = state.manager.soldiers.map(_.name)
      val deadSoldiers = playerViews.keys.toSet.diff(currentSoldiers)

      deadSoldiers.foreach: deadName =>
        val view = playerViews(deadName)
        gameView.removeElement(view)
        playerViews -= deadName

      state.powerUps.foreach: pu =>
        if !powerUpViews.contains(pu) then
          val tc = GeometryHelper.transform(pu.pos)
          val puType = pu.getClass.getSimpleName
          pu.shape match
            case ModelCircle(_, radius) =>
              val view = new PowerUpView(puType, tc.x, tc.y, GeometryHelper.transform(radius))
              powerUpViews += (pu -> view)
              gameView.addElements(view)
            case _ => ()

      //Remove powerup
      val powerUpsToRemove = powerUpViews.keys.toSet.diff(state.powerUps)
      powerUpsToRemove.foreach: pu =>
        val view = powerUpViews(pu)
        gameView.removeElement(view)
        powerUpViews -= pu

      state.projectile match
        case Some(proj) =>
          val tc = GeometryHelper.transform(proj.pos)

          projectileView match
            case Some(view) =>
              view.setPosition(tc.x, tc.y)
              trajectoryView.addPoint(tc.x, tc.y)

            case None =>
              val newProjView = new ProjectileView(tc.x, tc.y )
              projectileView = Some(newProjView)
              gameView.addElements(newProjView)
              trajectoryView.addPoint(tc.x, tc.y)

        case None =>
          projectileView.foreach: view =>
            gameView.removeElement(view)
          projectileView = None
          trajectoryView.clearTrajectory()
    trajectoryView.toBack()
    playerViews.values.foreach(_.toFront())

  private def showParsingError(message: String): Unit =
    gameView.showTemporaryError(message)