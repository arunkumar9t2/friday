package dev.arunkumar.jarvis.data.ticktick

import kotlinx.serialization.Serializable

@Serializable
data class TasksResponse(
  val projects: List<ApiTickTickProject> = emptyList(),
  val tasks: List<ApiTickTickTask> = emptyList(),
  val warnings: List<String> = emptyList(),
)
