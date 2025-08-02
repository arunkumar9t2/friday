package dev.arunkumar.jarvis.ui.screens.home

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
import dev.arunkumar.jarvis.ui.screens.HomeScreen
import dev.arunkumar.jarvis.ui.screens.SettingsScreen

@Composable
fun HomePresenter(
    navigator: Navigator
): HomeUiState {
    var counter by remember { mutableStateOf(0) }

    return HomeUiState(
        title = "Home",
        counter = counter,
        eventSink = { event ->
            when (event) {
                HomeUiEvent.OnIncrementClicked -> {
                    counter++
                }
                HomeUiEvent.OnNavigateToDetails -> {
                    navigator.goTo(DetailsScreen(
                        itemId = "item_$counter",
                        title = "Details for Item $counter"
                    ))
                }
                HomeUiEvent.OnNavigateToSettings -> {
                    navigator.goTo(SettingsScreen)
                }
            }
        }
    )
}

@Immutable
data class HomeUiState(
    val title: String,
    val counter: Int,
    val eventSink: (HomeUiEvent) -> Unit
) : CircuitUiState

sealed interface HomeUiEvent : CircuitUiEvent {
    object OnIncrementClicked : HomeUiEvent
    object OnNavigateToDetails : HomeUiEvent
    object OnNavigateToSettings : HomeUiEvent
}