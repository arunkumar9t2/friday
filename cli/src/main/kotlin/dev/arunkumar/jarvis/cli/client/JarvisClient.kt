package dev.arunkumar.jarvis.cli.client

import dev.arunkumar.jarvis.shared.models.ai.AiRequest
import dev.arunkumar.jarvis.shared.models.ai.AiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class JarvisClient(private val baseUrl: String) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun sendAiRequest(request: AiRequest): AiResponse {
        return client.post("$baseUrl/api/ai/chat") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    fun close() {
        client.close()
    }
}
