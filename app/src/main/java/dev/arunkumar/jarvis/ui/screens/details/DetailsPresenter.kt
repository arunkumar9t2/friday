package dev.arunkumar.jarvis.ui.screens.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import dev.arunkumar.jarvis.ui.screens.DetailsScreen

@Composable
fun DetailsPresenter(
    screen: DetailsScreen,
    navigator: Navigator
): DetailsUiState {
    var isFavorite by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(42) }

    return DetailsUiState(
        itemId = screen.itemId,
        title = screen.title,
        isFavorite = isFavorite,
        likeCount = likeCount,
        description = generateDescription(screen.itemId),
        eventSink = { event ->
            when (event) {
                DetailsUiEvent.OnBackClicked -> {
                    navigator.pop()
                }
                DetailsUiEvent.OnFavoriteToggled -> {
                    isFavorite = !isFavorite
                }
                DetailsUiEvent.OnLikeClicked -> {
                    likeCount++
                }
            }
        }
    )
}

private fun generateDescription(itemId: String): String {
    return """
        This is a detailed view for $itemId.
        
        Here you can see comprehensive information about this item, including its features, specifications, and other relevant details.
        
        This demonstrates Circuit's navigation capabilities with parameter passing and state management within individual screens.
        
        Features:
        • State preservation across configuration changes
        • Smooth navigation with Circuit
        • Dependency injection with Hilt
        • Modern Material Design 3 UI
    """.trimIndent()
}

@Immutable
data class DetailsUiState(
    val itemId: String,
    val title: String,
    val isFavorite: Boolean,
    val likeCount: Int,
    val description: String,
    val eventSink: (DetailsUiEvent) -> Unit
) : CircuitUiState

sealed interface DetailsUiEvent : CircuitUiEvent {
    object OnBackClicked : DetailsUiEvent
    object OnFavoriteToggled : DetailsUiEvent
    object OnLikeClicked : DetailsUiEvent
}