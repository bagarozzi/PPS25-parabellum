package it.unibo.parabellum.view

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.animation.AnimationTimer
import scalafx.Includes._
import scala.math.sin

object MainGUI extends JFXApp3:

  override def start(): Unit =

    // 1. Creazione Giocatore e Traiettoria
    val playerX = 100.0
    val playerY = 300.0

    val myPlayer = new PlayerView("Giocatore 1", playerX, playerY)
    val aimingCurve = new TrajectoryView()

    val ampiezza = 70.0
    val frequenza = 0.05

    val calcoloPunti = (0 to 120).map: i =>
      val x = playerX + (i * 5)
      val y = playerY - (ampiezza * sin((x - playerX) * frequenza))
      (x, y)

    aimingCurve.updateTrajectory(calcoloPunti)

    // 2. Creazione Proiettile
    val startPoint = calcoloPunti.head
    val myProjectile = new ProjectileView(startPoint._1, startPoint._2)

    // 3. Creazione Ostacoli
    val mountainObstacle = new ObstacleView()
    val mountainVertices = Seq(
      (250.0, 400.0),
      (300.0, 200.0),
      (350.0, 400.0)
    )
    mountainObstacle.drawShape(mountainVertices)

    val wallObstacle = new ObstacleView()
    val wallVertices = Seq(
      (550.0, 400.0),
      (550.0, 250.0),
      (600.0, 250.0),
      (600.0, 400.0)
    )
    wallObstacle.drawShape(wallVertices)

    // --- NUOVO: UTILIZZO DI GAMEVIEW ---
    val gameView = new GameView()

    // Passiamo tutti i nostri nodi alla GameView tramite il metodo che hai appena creato
    gameView.addElements(aimingCurve, myPlayer, mountainObstacle, wallObstacle, myProjectile)

    // Creiamo la scena di gioco assegnando la gameView come radice
    val gameScene = new Scene:
      root = gameView

    // 4. Loop di animazione
    var currentIndex = 0
    var frameTick = 0

    val gameLoop = AnimationTimer: time =>
      frameTick += 1
      if frameTick % 2 == 0 then
        if currentIndex < calcoloPunti.length then
          val (nextX, nextY) = calcoloPunti(currentIndex)
          myProjectile.setPosition(nextX, nextY)
          currentIndex += 1
        else
          currentIndex = 0

    // 5. Funzione per passare dal Menu al Gioco
    def avviaGioco(): Unit =
      stage.scene = gameScene
      gameLoop.start()

    // 6. Creazione Menu e Finestra
    val menuPane = new MenuView(avviaGioco)

    val menuScene = new Scene:
      fill = White
      root = menuPane

    stage = new JFXApp3.PrimaryStage:
      title = "Parabellum"
      width = 800
      height = 600
      resizable = false
      scene = menuScene