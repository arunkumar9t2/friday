package dev.arunkumar.jarvis.server.plugins

import dev.arunkumar.jarvis.server.routes.configureAiRoutes
import dev.arunkumar.jarvis.server.routes.configureHealthRoutes
import dev.arunkumar.jarvis.server.routes.configureUserRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        configureHealthRoutes()
        configureUserRoutes()
        configureAiRoutes()
    }
}
