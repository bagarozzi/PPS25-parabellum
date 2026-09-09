package it.unibo.parabellum
package view

import controller.{GameController, GameState}
import model.entity.{Obstacle, PowerUp}
import model.function.ParsingError
import model.shape.{Difference, Circle as ModelCircle, Polygon as ModelPolygon, Shape}
import util.BoundingBox
import view.TrajectoryView
import scalafx.scene.Node
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color.*

/**
 * Main GUI of the Parabellum game.
 *
 * Manages the UI lifecycle, mapping domain entities (players, obstacles, projectiles)
 * to ScalaFX nodes and orchestrating updates on each frame.
 *
 * @param width  Window width in pixels.
 * @param height Window height in pixels.
 */
class MainGUI(width: Double, height: Double) extends JFXApp3 with View:

  /** Implicit global window dimensions. */
  given windowSize: WindowSize = WindowSize(width, height)

  /** Main pane hosting the battlefield and game entities. */
  private lazy val gameView = new GameView(windowSize.width, windowSize.height)

  /** Dedicated visual layer for tracking the projectile's trajectory. */
  private val trajectoryView = new TrajectoryView()

  /**
   * Bottom panel for user input. Forwards mathematical expressions to the controller
   * and handles parsing errors.
   */
  private lazy val controlPanel = new ControlPanelView(userInput =>
    GameController.addProjectile(userInput) match
      case Some(ParsingError(message)) => showParsingError(message)
      case _ =>,
    () => endShootingMode()
  )

  // Tracking maps to sync immutable domain entities with their active visual nodes
  private var playerViews: Map[String, PlayerView] = Map.empty
  private var projectileView: Option[ProjectileView] = None
  private var obstacleViews: Map[Obstacle, ObstacleView] = Map.empty
  private var powerUpViews: Map[PowerUp, PowerUpView] = Map.empty

  /**
   * JavaFX application entry point.
   * Initializes the primary stage and sets up the main menu scene.
   */
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

    def startShootingRange(): Unit =
      GameController.startShootingRange()
      stage.scene = gameScene
      stage.sizeToScene()
      stage.centerOnScreen()

    val menuPane = new MenuView(avviaGioco, startShootingRange)
    val menuScene = new Scene(windowSize.width, windowSize.height):
      fill = White
      root = menuPane

    stage = new JFXApp3.PrimaryStage:
      title = "Parabellum"
      resizable = false
      scene = menuScene

  /**
   * Overrides the current scene to display the end-game screen.
   *
   * @param winner The name of the winning player/team.
   */
  override def showEndGame(winner: String): Unit =
    Platform.runLater(() => {
      val winnerPane = new EndGameView(() => restartGame(), height, width, winner)
      val winnerScene = new Scene:
        root = winnerPane

      stage.scene = winnerScene
    })

  /** Stops the shooting range mode and returns to the main menu. */
  private def endShootingMode(): Unit =
    Platform.runLater(() =>
      GameController.stopSession()
      restartGame()
    )

  /** Resets the visual tracking maps and reinitializes the application. */
  private def restartGame(): Unit =
    playerViews = Map.empty
    projectileView = None
    obstacleViews = Map.empty
    powerUpViews = Map.empty
    gameView.clear()
    start()

  /**
   * Visual garbage collection helper.
   * Removes orphaned views (e.g., dead soldiers, destroyed obstacles) from the scene and tracking map.
   *
   * @param activeKeys Set of currently valid entity keys.
   * @param views Map linking keys to their visual instances.
   * @return An updated map with stale associations removed.
   */
  private def removeStaleViews[K, V <: Node](activeKeys: Set[K], views: Map[K, V]): Map[K, V] =
    val staleKeys = views.keys.toSet.diff(activeKeys)
    staleKeys.foreach(k => gameView.removeElement(views(k)))
    views -- staleKeys

  /**
   * Translates domain shapes into drawing instructions for the `ObstacleView`,
   * applying scaling and translation via the bounding box.
   *
   * @param view The target obstacle view.
   * @param shape The domain shape (Circle or Polygon).
   * @param border The world's bounding box for coordinate scaling.
   */
  private def drawShape(view: ObstacleView, shape: Shape)(using border: BoundingBox): Unit = shape match
    case ModelCircle(center, radius) =>
      val tc = GeometryHelper.transform(center)
      view.drawCircle(tc.x, tc.y, GeometryHelper.transform(radius))
    case ModelPolygon(vertices) =>
      val screenVertices = vertices.map: v =>
        val tc = GeometryHelper.transform(v)
        (tc.x, tc.y)
      view.drawPolygon(screenVertices)
    case _ => ()

  /**
   * Core rendering pipeline, invoked on each game loop tick.
   * Synchronizes the immutable domain state with ScalaFX visual nodes.
   *
   * @param state The current game state snapshot.
   * @param border The map's bounding box for geometric transformations.
   */
  override def render(state: GameState)(using border: BoundingBox): Unit =
    Platform.runLater:
      controlPanel.updateCurrentPlayer(state.manager.current.name)

      //UPDATE OBSTACLES
      state.obstacles.foreach: obs =>
        if !obstacleViews.contains(obs) then
          val view = new ObstacleView()

          obs.shape match
            case diff @ Difference(baseShape, _) =>
              drawShape(view, baseShape)
              diff.diffSet.foreach:
                case ModelCircle(center, radius) =>
                  val tc = GeometryHelper.transform(center)
                  view.addHole(tc.x, tc.y, GeometryHelper.transform(radius))
                case _ => ()
            case basicShape =>
              drawShape(view, basicShape)

          obstacleViews += (obs -> view)
          gameView.addElements(view)

      obstacleViews = removeStaleViews(state.obstacles, obstacleViews)

      //UPDATE PLAYERS / SOLDIERS
      state.manager.soldiers.foreach: soldier =>
        val tc = GeometryHelper.transform(soldier.pos)

        playerViews.get(soldier.name) match
          case Some(view) =>
            view.setPosition(tc.x, tc.y)
            view.setHighlight(soldier == state.manager.current)
          case None =>
            val ModelCircle(_, radius) = soldier.shape.runtimeChecked
            val teamIndex = state.manager.teams.indexWhere(_.soldiers.contains(soldier))
            val newView = new PlayerView(soldier.name, tc.x, tc.y, GeometryHelper.transform(radius), teamIndex)
            playerViews += (soldier.name -> newView)
            gameView.addElements(newView)

      val currentSoldierNames = state.manager.soldiers.map(_.name)
      playerViews = removeStaleViews(currentSoldierNames, playerViews)

      //UPDATE POWER UPS
      state.powerUps.foreach: pu =>
        if !powerUpViews.contains(pu) then
          val tc = GeometryHelper.transform(pu.pos)
          pu.shape match
            case ModelCircle(_, radius) =>
              val view = new PowerUpView(pu.getClass.getSimpleName, tc.x, tc.y, GeometryHelper.transform(radius))
              powerUpViews += (pu -> view)
              gameView.addElements(view)
            case _ => ()

      powerUpViews = removeStaleViews(state.powerUps, powerUpViews)

      //UPDATE PROJECTILE
      state.projectile match
        case Some(proj) =>
          val tc = GeometryHelper.transform(proj.pos)
          projectileView match
            case Some(view) =>
              view.setPosition(tc.x, tc.y)
            case None =>
              val newProjView = new ProjectileView(tc.x, tc.y)
              projectileView = Some(newProjView)
              gameView.addElements(newProjView)

          trajectoryView.addPoint(tc.x, tc.y)

        case None =>
          projectileView.foreach(gameView.removeElement)
          projectileView = None
          trajectoryView.clearTrajectory()

      //VIEW ORDERING
      trajectoryView.toBack()
      playerViews.values.foreach(_.toFront())

  /**
   * Displays a temporary visual warning for invalid mathematical inputs.
   *
   * @param message The parsing error description generated by the Model.
   */
  private def showParsingError(message: String): Unit =
    gameView.showTemporaryError(message)