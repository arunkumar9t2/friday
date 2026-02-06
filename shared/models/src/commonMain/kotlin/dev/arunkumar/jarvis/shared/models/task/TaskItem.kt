package dev.arunkumar.jarvis.shared.models.task

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TaskItem(
    val id: String,
    val projectId: String,
    val title: String,
    val content: String? = null,
    val priority: TaskPriority = TaskPriority.NONE,
    val status: String,
    val dueDate: Instant? = null,
    val tags: List<String> = emptyList()
)
