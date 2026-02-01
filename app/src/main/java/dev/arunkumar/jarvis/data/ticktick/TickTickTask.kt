package dev.arunkumar.jarvis.data.ticktick

data class TickTickTask(
  val id: Long,
  val projectId: Long,
  val title: String,
  val dueDate: Long,
  val sortOrder: Long,
  val completedTime: Long,
  val priority: Int,
  val reminderTime: Long,
  val repeat: Boolean,
)
