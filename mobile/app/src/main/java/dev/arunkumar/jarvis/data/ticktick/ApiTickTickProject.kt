package dev.arunkumar.jarvis.data.ticktick

import kotlinx.serialization.Serializable

@Serializable
data class ApiTickTickProject(
  val id: String,
  val name: String,
  val color: String? = null,
  val sortOrder: Long = 0,
)
