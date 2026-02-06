package dev.arunkumar.jarvis.data.ticktick

import android.graphics.Color

enum class TickTickPriority(val level: Int, val color: Int) {
  NONE(0, Color.parseColor("#999999")),
  LOW(1, Color.parseColor("#496CF3")),
  MEDIUM_LOW(3, Color.parseColor("#F9CF1E")),
  MEDIUM(4, Color.parseColor("#F9CF1E")),
  HIGH(5, Color.parseColor("#C13541"));

  companion object {
    fun fromLevel(level: Int): TickTickPriority = when (level) {
      0 -> NONE
      1 -> LOW
      3 -> MEDIUM_LOW
      4 -> MEDIUM
      5 -> HIGH
      else -> NONE
    }
  }
}
