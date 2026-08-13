package it.unibo.parabellum.view

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color._
import scalafx.animation.AnimationTimer
import scala.math.sin

object MainGUI extends JFXApp3 {

  override def start(): Unit = {

    val playerX = 100.0
    val playerY = 300.0

    val myPlayer = new PlayerView("Giocatore 1", playerX, playerY)
    val aimingCurve = new TrajectoryView()

    // 1. Calcoliamo i punti della sinusoide
    val ampiezza = 70.0
    val frequenza = 0.05

    
    val calcoloPunti = (0 to 120).map { i =>
      val x = playerX + (i * 5)
      val y = playerY - (ampiezza * sin((x - playerX) * frequenza))
      (x, y)
    }

    aimingCurve.updateTrajectory(calcoloPunti)

    // 2. Creiamo il proiettile facendolo partire dal primo punto della curva
    val startPoint = calcoloPunti.head
    val myProjectile = new ProjectileView(startPoint._1, startPoint._2)

    // --- NUOVO: OSTACOLI SULLA TRAIETTORIA ---

    // 3. Spostiamo la "Montagna" in modo che tagli la prima curva.
    // La punta arriva a Y=200, intercettando in pieno la sinusoide.
    val mountainObstacle = new ObstacleView()
    val mountainVertices = Seq(
      (250.0, 400.0), // Base sinistra
      (300.0, 200.0), // Punta in alto
      (350.0, 400.0)  // Base destra
    )
    mountainObstacle.drawShape(mountainVertices)

    // 4. Spostiamo il "Muro" più avanti (X=550), facendolo alto abbastanza da bloccare il colpo
    val wallObstacle = new ObstacleView()
    val wallVertices = Seq(
      (550.0, 400.0), // In basso a sinistra
      (550.0, 250.0), // In alto a sinistra
      (600.0, 250.0), // In alto a destra
      (600.0, 400.0)  // In basso a destra
    )
    wallObstacle.drawShape(wallVertices)

    // --- FINE OSTACOLI ---

    // 5. Creiamo il Pane che fa da contenitore radice.
    val gamePane = new Pane {
      children = List(aimingCurve, myPlayer, mountainObstacle, wallObstacle, myProjectile)
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Interfaccia di Base - Gioco con Ostacoli sulla Traiettoria"
      width = 800
      height = 600
      resizable = false

      scene = new Scene {
        fill = DarkGray
        content = gamePane
      }
    }

    // 6. Loop di animazione
    var currentIndex = 0
    var frameTick = 0

    val gameLoop = AnimationTimer { time =>
      frameTick += 1

      if (frameTick % 2 == 0) {
        if (currentIndex < calcoloPunti.length) {
          val (nextX, nextY) = calcoloPunti(currentIndex)
          myProjectile.setPosition(nextX, nextY)
          currentIndex += 1
        } else {
          currentIndex = 0
        }
      }
    }

    gameLoop.start()
  }
}