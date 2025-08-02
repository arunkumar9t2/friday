package dev.arunkumar.jarvis.ui.screens.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.ui.screens.DetailsScreen

class DetailsPresenter @AssistedInject constructor(
    @Assisted private val screen: DetailsScreen,
    @Assisted private val navigator: Navigator,
) : Presenter<DetailsScreen.State> {

    @Composable
    override fun present(): DetailsScreen.State {
        var isFavorite by remember { mutableStateOf(false) }
        var likeCount by remember { mutableStateOf(42) }

        return DetailsScreen.State(
            itemId = screen.itemId,
            title = screen.title,
            isFavorite = isFavorite,
            likeCount = likeCount,
            description = generateDescription(screen.itemId),
            eventSink = { event ->
                when (event) {
                    DetailsScreen.Event.OnBackClicked -> {
                        navigator.pop()
                    }
                    DetailsScreen.Event.OnFavoriteToggled -> {
                        isFavorite = !isFavorite
                    }
                    DetailsScreen.Event.OnLikeClicked -> {
                        likeCount++
                    }
                }
            }
        )
    }

    @CircuitInject(DetailsScreen::class, ActivityComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: DetailsScreen,
            navigator: Navigator,
        ): DetailsPresenter
    }
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

