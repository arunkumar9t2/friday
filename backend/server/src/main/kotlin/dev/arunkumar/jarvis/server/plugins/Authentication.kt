package dev.arunkumar.jarvis.server.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuthentication() {
    // TODO: Configure Firebase JWT authentication
    // Placeholder for now
    install(Authentication) {
        // Will be configured with Firebase Admin SDK
    }
}
