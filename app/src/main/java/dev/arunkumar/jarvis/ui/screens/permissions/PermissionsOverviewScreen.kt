package dev.arunkumar.jarvis.ui.screens.permissions

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.arunkumar.jarvis.data.permissions.FeatureGroup
import dev.arunkumar.jarvis.data.permissions.FeatureGroupState
import dev.arunkumar.jarvis.ui.state.ListState
import kotlinx.parcelize.Parcelize

/** Screen for displaying overview of all permission groups and their status */
@Parcelize
data class PermissionsOverviewScreen(
  val launchMode: LaunchMode = LaunchMode.SETTINGS
) : Screen {

  enum class LaunchMode {
    SETTINGS,    // Launched from settings
    ONBOARDING   // Launched for first-time setup
  }

  sealed interface Message {
    val text: String
    data class Success(override val text: String) : Message
    data class Error(override val text: String) : Message
  }

  @Immutable
  data class State(
    val featureGroupsState: ListState<FeatureGroupState>,
    val launchMode: LaunchMode,
    val message: Message? = null,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object OnRefresh : Event
    data object OnNavigateBack : Event
    data object OnMessageDismissed : Event
    data class OnFeatureGroupClick(val group: FeatureGroup) : Event
  }
}
