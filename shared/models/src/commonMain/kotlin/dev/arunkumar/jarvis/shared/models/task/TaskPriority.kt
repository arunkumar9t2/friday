package dev.arunkumar.jarvis.shared.models.task

import kotlinx.serialization.Serializable

@Serializable
enum class TaskPriority(val level: Int) {
    NONE(0),
    LOW(1),
    MEDIUM(3),
    HIGH(5)
}
