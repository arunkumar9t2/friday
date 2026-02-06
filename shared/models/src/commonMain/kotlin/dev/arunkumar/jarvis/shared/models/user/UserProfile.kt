package dev.arunkumar.jarvis.shared.models.user

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null
)
