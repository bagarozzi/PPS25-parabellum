package it.unibo.parabellum.view

import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.paint.Color.*
import scalafx.scene.layout.BorderPane
import scalafx.Includes.*
import it.unibo.parabellum.model.entity.Player
import it.unibo.parabellum.model.function.Projectile
import it.unibo.parabellum.controller.{GameController, GameState}
import it.unibo.parabellum.model.collision.CollisionDetector

class MainGUI(width: Double, height: Double) extends JFXApp3 with View:

  given windowSize: WindowSize = WindowSize(width, height)
  import CollisionDetector.given

  // Riferimenti ai componenti grafici per poterli aggiornare o rimuovere
  private val gameView = new GameView(windowSize.width, windowSize.height)
  private var playerViews: Map[String, PlayerView] = Map.empty
  private var projectileView: Option[ProjectileView] = None

  override def start(): Unit =

    // --- SETUP DELLA SCENA ---
    val controlPanel = new ControlPanelView(pendenza =>
      println(s"Hai premuto SPARA! Pendenza inserita: $pendenza")
      val angularCoefficient: Option[Double] = pendenza.toDoubleOption
      GameController.addProjectile(angularCoefficient.get)
    )

    val rootPane = new BorderPane:
      center = gameView
      bottom = controlPanel

    val gameScene = new Scene:
      root = rootPane

    def avviaGioco(): Unit = {
      GameController.startGame()
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
  /**
   * Metodo chiamato dall'Engine a ogni frame per aggiornare la grafica.
   */
  override def render(state: GameState): Unit =
    Platform.runLater:

      // 1. Aggiorna o crea i Giocatori usando le coordinate dirette (già scalate)
      state.players.foreach: player =>
        val tc = GeometryHelper.transform(player.pos)

        playerViews.get(player.name) match
          case Some(view) =>
            // Il giocatore esiste già, aggiorniamo solo la posizione
            view.setPosition(tc.x, tc.y)
          case None =>
            // Primo frame in cui vediamo questo giocatore, lo creiamo
            val newView = new PlayerView(player.name, tc.x, tc.y)
            playerViews += (player.name -> newView)
            gameView.addElements(newView)

      // 2. Aggiorna, crea o rimuove il Proiettile
      state.projectiles match
        case Some(proj) =>
            val tc = GeometryHelper.transform(proj.pos())

            projectileView match
              case Some(view) =>
                // Proiettile in volo, aggiorniamo la posizione
                view.setPosition(tc.x, tc.y)
              case None =>
                // Sparato un nuovo proiettile, creiamo il nodo a schermo
                val newProjView = new ProjectileView(tc.x, tc.y )
                projectileView = Some(newProjView)
                gameView.addElements(newProjView)

        case None =>
          // Non c'è un proiettile nello stato, se è presente a schermo lo rimuoviamo
          projectileView.foreach: view =>
            gameView.removeElement(view)
          projectileView = None