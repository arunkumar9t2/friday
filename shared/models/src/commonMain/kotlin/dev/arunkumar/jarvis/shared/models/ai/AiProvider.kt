package dev.arunkumar.jarvis.shared.models.ai

import kotlinx.serialization.Serializable

@Serializable
enum class AiProvider {
    VERTEX_GEMINI,
    CLAUDE_API,
    OPENAI
}
