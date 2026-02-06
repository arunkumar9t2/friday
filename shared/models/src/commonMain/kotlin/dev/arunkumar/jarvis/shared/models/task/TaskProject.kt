package dev.arunkumar.jarvis.shared.models.task

import kotlinx.serialization.Serializable

@Serializable
data class TaskProject(
    val id: String,
    val name: String,
    val color: String? = null,
    val sortOrder: Int = 0
)
