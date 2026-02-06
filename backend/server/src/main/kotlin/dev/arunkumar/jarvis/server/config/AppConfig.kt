package dev.arunkumar.jarvis.server.config

object AppConfig {
    val port: Int = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val gcpProjectId: String = System.getenv("GCP_PROJECT_ID") ?: "jarvis-dev"
    val environment: String = System.getenv("ENVIRONMENT") ?: "development"
}
