package it.unibo.parabellum
package util

/**
 * Utilities to turn the names typed by the users into names that can safely identify the players of a match.
 */
object PlayerNames:

  /**
   * Cleans up the names of the players: surrounding spaces are removed, blank names are replaced with
   * "Player N" (N being the position of the player) and duplicates get a numeric suffix.
   * Player (and soldier) names are used as identifiers, so they have to be different from each other.
   *
   * @param raw the names as typed by the users, in turn order
   * @return names that are not blank and all different, in the same order
   */
  def normalize(raw: Seq[String]): Vector[String] =
    raw.zipWithIndex.foldLeft(Vector.empty[String]): (acc, entry) =>
      val (name, index) = entry
      val base = name.trim match
        case "" => s"Player ${index + 1}"
        case trimmed => trimmed
      acc :+ firstAvailable(base, acc)

  private def firstAvailable(base: String, taken: Seq[String]): String =
    if !taken.contains(base) then base
    else Iterator.from(2).map(n => s"$base ($n)").find(!taken.contains(_)).get
