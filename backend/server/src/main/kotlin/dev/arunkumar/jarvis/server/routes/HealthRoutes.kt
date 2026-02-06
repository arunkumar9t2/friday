package dev.arunkumar.jarvis.server.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val status: String)

fun Route.configureHealthRoutes() {
    get("/health") {
        call.respond(HttpStatusCode.OK, HealthResponse("ok"))
    }
}
