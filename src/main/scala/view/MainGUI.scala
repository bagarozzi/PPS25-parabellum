package it.unibo.parabellum.view

import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.paint.Color.*
import scalafx.scene.layout.BorderPane
import scalafx.Includes.*
import it.unibo.parabellum.model.entity.{Obstacle, Player}
import it.unibo.parabellum.model.function.Projectile
import it.unibo.parabellum.controller.{GameController, GameState}
import it.unibo.parabellum.model.collision.CollisionDetector
import it.unibo.parabellum.view.TrajectoryView
import it.unibo.parabellum.model.shape.{Difference, Circle as ModelCircle, Polygon as ModelPolygon}

import scala.collection.StepperShape.Shape

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
  import CollisionDetector.given

  private val gameView = new GameView(windowSize.width, windowSize.height)
  private val trajectoryView = new TrajectoryView()
  private lazy val controlPanel = new ControlPanelView(userInput =>
    GameController.addProjectile(userInput)
  )
  private var playerViews: Map[String, PlayerView] = Map.empty
  private var projectileView: Option[ProjectileView] = None
  private var obstacleViews: Map[Obstacle, ObstacleView] = Map.empty

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

  override def render(state: GameState): Unit =
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
          case None =>
            val ModelCircle(pos, radius) = soldier.shape.runtimeChecked   
            val newView = new PlayerView(soldier.name, tc.x, tc.y, GeometryHelper.transform(radius))
            playerViews += (soldier.name -> newView)
            gameView.addElements(newView)

      state.projectile match
        case Some(proj) =>
          val tc = GeometryHelper.transform(proj.pos())

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