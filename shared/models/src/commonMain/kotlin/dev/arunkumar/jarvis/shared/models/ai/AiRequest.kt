package dev.arunkumar.jarvis.shared.models.ai

import kotlinx.serialization.Serializable

@Serializable
data class AiRequest(
    val provider: AiProvider,
    val prompt: String,
    val systemInstruction: String? = null,
    val maxTokens: Int? = null,
    val temperature: Double? = null
)
