package dev.arunkumar.jarvis.data.ticktick

import kotlinx.serialization.Serializable

@Serializable
data class ApiTickTickTask(
  val id: String,
  val projectId: String,
  val title: String,
  val content: String? = null,
  val priority: Int = 0, // 0=none, 1=low, 3=medium, 5=high
  val status: Int = 0, // 0=incomplete, 2=completed
  val dueDate: String? = null, // ISO 8601: "2024-01-15T10:30:00.000+0000"
  val startDate: String? = null,
  val isAllDay: Boolean = false,
  val tags: List<ApiTickTickTag> = emptyList(),
  val sortOrder: Long = 0,
  val completedTime: String? = null,
  val createdTime: String? = null,
  val modifiedTime: String? = null,
)
