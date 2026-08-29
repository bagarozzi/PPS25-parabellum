package it.unibo.parabellum.util

import alice.tuprolog.{Prolog, Struct, Theory}

object PrologMapChecker:

  private val engine = new Prolog()

  private val theory = new Theory(
    """
      distance(X1, Y1, X2, Y2, D) :- D is sqrt((X2 - X1)**2 + (Y2 - Y1)**2).
      overlap(X1, Y1, R1, X2, Y2, R2) :- distance(X1, Y1, X2, Y2, D), D < (R1 + R2).
      check_overlap_list(X, Y, R, [element(EX, EY, ER) | _]) :- overlap(X, Y, R, EX, EY, ER), !.
      check_overlap_list(X, Y, R, [_ | Tail]) :- check_overlap_list(X, Y, R, Tail).
    """
  )
  engine.setTheory(theory)

  /**
   * Verifies if the new Obstacles will collide
   * @param existing Sequence of tuples (X, Y, Radius)
   */
  def hasOverlap(newX: Double, newY: Double, newR: Double, existing: Seq[(Double, Double, Double)]): Boolean =
    if existing.isEmpty then return false
    val listTermStr = existing.map(e => s"element(${e._1}, ${e._2}, ${e._3})").mkString("[", ",", "]")
    val queryStr = s"check_overlap_list($newX, $newY, $newR, $listTermStr)."
    val info = engine.solve(queryStr)
    info.isSuccess