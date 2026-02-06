package dev.arunkumar.jarvis.shared.models.ai

import kotlinx.serialization.Serializable

@Serializable
data class AiResponse(
    val provider: AiProvider,
    val content: String,
    val model: String,
    val tokensUsed: Int? = null,
    val durationMs: Long? = null
)
