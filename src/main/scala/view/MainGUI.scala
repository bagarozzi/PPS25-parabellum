package it.unibo.parabellum.view

import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.layout.BorderPane
import scalafx.Includes._
import it.unibo.parabellum.model.entity.Player
import it.unibo.parabellum.model.function.Projectile
import it.unibo.parabellum.controller.GameState

object MainGUI extends JFXApp3 with View:

  // Riferimenti ai componenti grafici per poterli aggiornare o rimuovere
  private val gameView = new GameView()
  private var playerViews: Map[String, PlayerView] = Map.empty
  private var projectileView: Option[ProjectileView] = None

  override def start(): Unit =

    // --- (Opzionale) Manteniamo gli ostacoli statici per ora ---
    val mountainObstacle = new ObstacleView()
    val mountainVertices = Seq((250.0, 400.0), (300.0, 200.0), (350.0, 400.0))
    mountainObstacle.drawShape(mountainVertices)

    val wallObstacle = new ObstacleView()
    val wallVertices = Seq((550.0, 400.0), (550.0, 250.0), (600.0, 250.0), (600.0, 400.0))
    wallObstacle.drawShape(wallVertices)

    gameView.addElements(mountainObstacle, wallObstacle)

    // --- SETUP DELLA SCENA ---
    val controlPanel = new ControlPanelView(pendenza =>
      println(s"Hai premuto SPARA! Pendenza inserita: $pendenza")
      // In futuro qui potrai chiamare un metodo del Controller per notificare lo sparo
    )

    val rootPane = new BorderPane:
      center = gameView
      bottom = controlPanel

    val gameScene = new Scene(800, 600):
      root = rootPane

    def avviaGioco(): Unit =
      stage.scene = gameScene

    val menuPane = new MenuView(avviaGioco)
    val menuScene = new Scene(800, 600):
      fill = White
      root = menuPane

    stage = new JFXApp3.PrimaryStage:
      title = "Parabellum"
      resizable = false
      scene = menuScene

  /**
   * Metodo chiamato dall'Engine a ogni frame per aggiornare la grafica.
   */
  override def render(state: GameState): Unit =
    Platform.runLater:

      // 1. Aggiorna o crea i Giocatori usando le coordinate dirette (già scalate)
      state.players.foreach: player =>
        val px = player.pos.x
        val py = player.pos.y

        playerViews.get(player.name) match
          case Some(view) =>
            // Il giocatore esiste già, aggiorniamo solo la posizione
            view.setPosition(px, py)
          case None =>
            // Primo frame in cui vediamo questo giocatore, lo creiamo
            val newView = new PlayerView(player.name, px, py)
            playerViews += (player.name -> newView)
            gameView.addElements(newView)

      // 2. Aggiorna, crea o rimuove il Proiettile
      state.projectiles match
        case Some(proj) =>
          val projX = proj.pos.x
          val projY = proj.pos.y

          projectileView match
            case Some(view) =>
              // Proiettile in volo, aggiorniamo la posizione
              view.setPosition(projX, projY)
            case None =>
              // Sparato un nuovo proiettile, creiamo il nodo a schermo
              val newProjView = new ProjectileView(projX, projY)
              projectileView = Some(newProjView)
              gameView.addElements(newProjView)

        case None =>
          // Non c'è un proiettile nello stato, se è presente a schermo lo rimuoviamo
          projectileView.foreach: view =>
            gameView.removeElement(view)
          projectileView = None