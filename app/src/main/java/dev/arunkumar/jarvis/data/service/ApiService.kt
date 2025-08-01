package dev.arunkumar.jarvis.data.service

import dev.arunkumar.jarvis.data.repository.UserProfile
import dev.arunkumar.jarvis.data.repository.UserSettings
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor() {
    
    suspend fun fetchUserProfile(userId: String): UserProfile {
        // Simulate network delay
        delay(1000)
        
        return UserProfile(
            id = userId,
            name = "John Doe",
            email = "john.doe@example.com",
            avatarUrl = "https://example.com/avatar.jpg"
        )
    }

    suspend fun updateUserSettings(settings: UserSettings) {
        // Simulate network delay
        delay(500)
        
        // Mock successful update
        println("User settings updated: $settings")
    }

    suspend fun fetchRecommendations(userId: String): List<Recommendation> {
        delay(800)
        
        return listOf(
            Recommendation(
                id = "rec_1",
                title = "Morning Routine Optimization",
                description = "Based on your schedule, here are some tips to optimize your morning routine.",
                priority = Priority.HIGH
            ),
            Recommendation(
                id = "rec_2",
                title = "Weekly Planning Session",
                description = "Schedule a weekly planning session to stay on top of your goals.",
                priority = Priority.MEDIUM
            ),
            Recommendation(
                id = "rec_3",
                title = "Take a Break",
                description = "You've been working hard! Consider taking a short break.",
                priority = Priority.LOW
            )
        )
    }
}

data class Recommendation(
    val id: String,
    val title: String,
    val description: String,
    val priority: Priority
)

enum class Priority {
    HIGH, MEDIUM, LOW
}