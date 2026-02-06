package dev.arunkumar.jarvis.shared.models.user

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "en"
)
