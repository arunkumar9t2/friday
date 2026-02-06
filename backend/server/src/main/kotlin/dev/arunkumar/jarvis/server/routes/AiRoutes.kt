package dev.arunkumar.jarvis.server.routes

import dev.arunkumar.jarvis.shared.models.ai.AiRequest
import dev.arunkumar.jarvis.shared.models.ai.AiResponse
import dev.arunkumar.jarvis.shared.models.ai.AiProvider
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureAiRoutes() {
    route("/api/ai") {
        post("/chat") {
            val request = call.receive<AiRequest>()
            // TODO: Implement actual AI provider integration
            val placeholder = AiResponse(
                provider = request.provider,
                content = "AI response placeholder",
                model = "placeholder-model"
            )
            call.respond(HttpStatusCode.OK, placeholder)
        }
    }
}
