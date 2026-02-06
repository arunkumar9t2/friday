package dev.arunkumar.jarvis.server

import dev.arunkumar.jarvis.server.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureAuthentication()
    configureRouting()
}
