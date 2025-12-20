package dev.arunkumar.jarvis.ui.screens.permissions

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.arunkumar.jarvis.data.permissions.FeatureGroup
import dev.arunkumar.jarvis.data.permissions.PermissionState
import dev.arunkumar.jarvis.data.permissions.PermissionType
import dev.arunkumar.jarvis.ui.state.ListState
import kotlinx.parcelize.Parcelize

/** Screen for displaying detailed permissions within a feature group */
@Parcelize
data class FeatureGroupDetailScreen(
  val featureGroup: FeatureGroup
) : Screen {

  sealed interface Message {
    val text: String
    data class Success(override val text: String) : Message
    data class Error(override val text: String) : Message
  }

  @Immutable
  data class State(
    val featureGroup: FeatureGroup,
    val permissionsState: ListState<PermissionState>,
    val message: Message? = null,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object OnNavigateBack : Event
    data object OnRefresh : Event
    data object OnMessageDismissed : Event
    data class OnRequestPermission(val permission: PermissionType) : Event
    data class OnOpenSettings(val permission: PermissionType) : Event
  }
}
