package dev.arunkumar.jarvis.data.repository

import dev.arunkumar.jarvis.data.service.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserProfile(userId: String): UserProfile {
        return apiService.fetchUserProfile(userId)
    }

    fun getUserSettings(): Flow<UserSettings> {
        return flowOf(
            UserSettings(
                isDarkTheme = false,
                notificationsEnabled = true,
                language = "English"
            )
        )
    }

    suspend fun updateUserSettings(settings: UserSettings) {
        // Mock update operation
        apiService.updateUserSettings(settings)
    }
}

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null
)

data class UserSettings(
    val isDarkTheme: Boolean,
    val notificationsEnabled: Boolean,
    val language: String
)