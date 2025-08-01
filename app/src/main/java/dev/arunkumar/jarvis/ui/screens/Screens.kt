package dev.arunkumar.jarvis.ui.screens

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.screen.Screen

/**
 * Sealed interface representing all the screens in the app.
 * Each screen is a data class that can hold navigation arguments.
 */
sealed interface AppScreen : Screen

@Immutable
object HomeScreen : AppScreen {
    override fun toString() = "HomeScreen"
}

@Immutable
data class DetailsScreen(
    val itemId: String,
    val title: String
) : AppScreen {
    override fun toString() = "DetailsScreen($itemId, $title)"
}

@Immutable
object SettingsScreen : AppScreen {
    override fun toString() = "SettingsScreen"
}

@Immutable
data class ProfileScreen(
    val userId: String
) : AppScreen {
    override fun toString() = "ProfileScreen($userId)"
}