package it.unibo.parabellum
package view

import session.{GameSession, LoginExpired, OnlineAccount, OnlineSessions, SessionFactory}
import snapshot.{CircleSnapshot, GameSnapshot, ObstacleSnapshot, PolygonSnapshot, PowerUpSnapshot, ShapeSnapshot}
import util.BoundingBox
import view.TrajectoryView
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
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
 * @param width    Window width in pixels.
 * @param height   Window height in pixels.
 * @param sessions Starts the matches that the user plays on this machine.
 * @param online   Starts the matches that the user plays on a server.
 */
class MainGUI(width: Double, height: Double, sessions: SessionFactory, online: OnlineSessions) extends JFXApp3 with View:

  /** Implicit global window dimensions. */
  given windowSize: WindowSize = WindowSize(width, height)

  /** Main pane hosting the battlefield and game entities. */
  private lazy val gameView = new GameView(windowSize.width, windowSize.height)

  /** Dedicated visual layer for tracking the projectile's trajectory. */
  private val trajectoryView = new TrajectoryView()

  /** The match the user is playing, if any. */
  private var currentSession: Option[GameSession] = None

  /** The user that is logged in on a server, if any: he stays logged in between the matches. */
  private var account: Option[OnlineAccount] = None

  /** The server of the last login, suggested for the next one. */
  private var lastServer: String = "127.0.0.1:8080"

  /**
   * Bottom panel for user input. Forwards the shots to the current session,
   * which reports the invalid ones back through [[showError]].
   */
  private lazy val controlPanel = new ControlPanelView(
    (userInput, direction) => currentSession.foreach(_.submit(userInput, direction)),
    () => quitToMenu()
  )

  // Tracking maps to sync the entities described by the snapshots with their active visual nodes
  private var playerViews: Map[String, PlayerView] = Map.empty
  private var projectileView: Option[ProjectileView] = None
  private var obstacleViews: Map[ObstacleSnapshot, ObstacleView] = Map.empty
  private var powerUpViews: Map[PowerUpSnapshot, PowerUpView] = Map.empty

  // The look of a team (its color) is decided the first time the players are seen and then never changes,
  // even when other teams are eliminated.
  private var teamIndices: Map[String, Int] = Map.empty

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

    /** Shows the battlefield and plays the session. */
    def enterGame(session: GameSession): Unit =
      currentSession = Some(session)
      stage.scene = gameScene
      stage.sizeToScene()
      stage.centerOnScreen()

    def avviaGioco(players: Seq[String], soldiers: Int): Unit =
      enterGame(sessions.newMatch(players, soldiers, this))

    def startShootingRange(): Unit =
      enterGame(sessions.shootingRange(this))

    /** Waits for a login (or a registration followed by a login): if it works the user is logged in. */
    def awaitLogin(server: String, attempt: Future[OnlineAccount], welcome: String): Unit =
      menuView.setBusy(true)
      attempt.onComplete: result =>
        Platform.runLater(() => {
          menuView.setBusy(false)
          result match
            case Success(user) =>
              account = Some(user)
              lastServer = server
              menuView.showLoggedIn(user)
              menuView.showMessage(welcome)
            case Failure(error) => menuView.showMessage(error.getMessage)
        })

    def logIn(server: String, username: String, password: String): Unit =
      awaitLogin(server, online.login(server, username, password), "")

    /** Creates an account on the server and logs in with it at once. */
    def registerAccount(server: String, username: String, password: String): Unit =
      awaitLogin(server, online.register(server, username, password).flatMap(_ => online.login(server, username, password)), "Account created")

    def logOut(): Unit =
      account = None
      menuView.showLoggedOut()

    /** Goes back to the main menu because the login is not valid any more. */
    def loginExpired(error: LoginExpired): Unit =
      logOut()
      stage.scene = menuScene
      menuView.showMessage(error.getMessage)

    /** Waits for the connection to a server: if it works the match starts, otherwise the user is told why. */
    def playOnline(onlineView: OnlineView, connection: Future[GameSession]): Unit =
      onlineView.setBusy(true)
      connection.onComplete: result =>
        Platform.runLater(() => {
          onlineView.setBusy(false)
          result match
            case Success(session) => enterGame(session)
            case Failure(error: LoginExpired) => loginExpired(error)
            case Failure(error) => onlineView.showMessage(error.getMessage)
        })

    /** The online screen is only for who is logged in, and is made for him. */
    def openOnline(): Unit =
      account.foreach: user =>
        lazy val onlineView: OnlineView = new OnlineView(
          user,
          matchId => playOnline(onlineView, online.join(user, matchId, this)),
          (matchId, others, soldiers) => playOnline(onlineView, online.create(user, matchId, others, soldiers, this)),
          () => stage.scene = menuScene
        )
        stage.scene = new Scene(windowSize.width, windowSize.height):
          fill = White
          root = onlineView

    lazy val menuView: MenuView = new MenuView(
      avviaGioco,
      startShootingRange,
      () => openOnline(),
      (server, username, password) => logIn(server, username, password),
      (server, username, password) => registerAccount(server, username, password),
      () => logOut(),
      account,
      lastServer
    )

    lazy val menuScene: Scene = new Scene(windowSize.width, windowSize.height):
      fill = White
      root = menuView

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

  /** Leaves the current session and returns to the main menu. */
  private def quitToMenu(): Unit =
    Platform.runLater(() => restartGame())

  /** Leaves the current session, resets the visual tracking maps and reinitializes the application. */
  private def restartGame(): Unit =
    currentSession.foreach(_.close())
    currentSession = None
    playerViews = Map.empty
    projectileView = None
    obstacleViews = Map.empty
    powerUpViews = Map.empty
    teamIndices = Map.empty
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
   * Translates the shapes of a snapshot into drawing instructions for the `ObstacleView`,
   * applying scaling and translation via the bounding box.
   *
   * @param view The target obstacle view.
   * @param shape The shape to draw (circle or polygon).
   * @param border The world's bounding box for coordinate scaling.
   */
  private def drawShape(view: ObstacleView, shape: ShapeSnapshot)(using border: BoundingBox): Unit = shape match
    case CircleSnapshot(center, radius) =>
      val tc = GeometryHelper.transform(center)
      view.drawCircle(tc.x, tc.y, GeometryHelper.transform(radius))
    case PolygonSnapshot(vertices) =>
      val screenVertices = vertices.map: v =>
        val tc = GeometryHelper.transform(v)
        (tc.x, tc.y)
      view.drawPolygon(screenVertices)

  /**
   * The index of the team of a player, used to give each team its own look.
   * The indices are assigned when the first snapshot of a match arrives, following the turn order.
   *
   * @param owner The name of the player.
   * @param snapshot The snapshot being rendered.
   */
  private def teamIndexOf(owner: String, snapshot: GameSnapshot): Int =
    if teamIndices.isEmpty then teamIndices = snapshot.players.zipWithIndex.toMap
    teamIndices.get(owner) match
      case Some(index) => index
      case None =>
        val index = teamIndices.size
        teamIndices += (owner -> index)
        index

  /**
   * Core rendering pipeline, invoked on each game loop tick.
   * Synchronizes the state of the match, described by a snapshot, with ScalaFX visual nodes.
   * It only needs the snapshot: it never touches the game model.
   *
   * @param snapshot The current snapshot of the match.
   * @param border The map's bounding box for geometric transformations.
   */
  override def render(snapshot: GameSnapshot)(using border: BoundingBox): Unit =
    Platform.runLater:
      controlPanel.updateCurrentPlayer(snapshot.turn.soldier, snapshot.turn.suggestedDirection)

      //UPDATE OBSTACLES
      snapshot.obstacles.foreach: obs =>
        if !obstacleViews.contains(obs) then
          val view = new ObstacleView()
          drawShape(view, obs.shape)
          obs.holes.foreach: hole =>
            val tc = GeometryHelper.transform(hole.center)
            view.addHole(tc.x, tc.y, GeometryHelper.transform(hole.radius))

          obstacleViews += (obs -> view)
          gameView.addElements(view)

      obstacleViews = removeStaleViews(snapshot.obstacles, obstacleViews)

      //UPDATE PLAYERS / SOLDIERS
      snapshot.soldiers.foreach: soldier =>
        val tc = GeometryHelper.transform(soldier.pos)

        playerViews.get(soldier.name) match
          case Some(view) =>
            view.setPosition(tc.x, tc.y)
            view.setHighlight(soldier.name == snapshot.turn.soldier)
          case None =>
            val newView = new PlayerView(soldier.name, tc.x, tc.y, GeometryHelper.transform(soldier.radius), teamIndexOf(soldier.owner, snapshot))
            playerViews += (soldier.name -> newView)
            gameView.addElements(newView)

      val currentSoldierNames = snapshot.soldiers.map(_.name)
      playerViews = removeStaleViews(currentSoldierNames, playerViews)

      //UPDATE POWER UPS
      snapshot.powerUps.foreach: pu =>
        if !powerUpViews.contains(pu) then
          val tc = GeometryHelper.transform(pu.pos)
          val view = new PowerUpView(pu.kind.toString, tc.x, tc.y, GeometryHelper.transform(pu.radius))
          powerUpViews += (pu -> view)
          gameView.addElements(view)

      powerUpViews = removeStaleViews(snapshot.powerUps, powerUpViews)

      //UPDATE PROJECTILE
      snapshot.projectile match
        case Some(pos) =>
          val tc = GeometryHelper.transform(pos)
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
   * @param message The description of the error, already formatted for the user.
   */
  override def showError(message: String): Unit =
    Platform.runLater(() => gameView.showTemporaryError(message))