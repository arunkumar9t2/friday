package dev.arunkumar.jarvis.server.routes

import dev.arunkumar.jarvis.shared.models.user.UserProfile
import dev.arunkumar.jarvis.shared.models.user.UserSettings
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureUserRoutes() {
    route("/api/user") {
        get("/profile") {
            // TODO: Get from Firestore
            val placeholder = UserProfile(
                id = "demo",
                name = "Demo User",
                email = "demo@jarvis.dev"
            )
            call.respond(HttpStatusCode.OK, placeholder)
        }

        get("/settings") {
            // TODO: Get from Firestore
            val placeholder = UserSettings()
            call.respond(HttpStatusCode.OK, placeholder)
        }
    }
}
