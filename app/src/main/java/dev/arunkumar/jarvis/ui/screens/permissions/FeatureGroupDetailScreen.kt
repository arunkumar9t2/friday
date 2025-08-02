package dev.arunkumar.jarvis.ui.screens.permissions

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.arunkumar.jarvis.data.permissions.FeatureGroup
import dev.arunkumar.jarvis.data.permissions.PermissionState
import dev.arunkumar.jarvis.data.permissions.PermissionType
import kotlinx.parcelize.Parcelize

/** Screen for displaying detailed permissions within a feature group */
@Parcelize
data class FeatureGroupDetailScreen(
  val featureGroup: FeatureGroup
) : Screen {

  @Stable
  @Immutable
  data class State(
    val featureGroup: FeatureGroup,
    val permissions: List<PermissionState>,
    val isLoading: Boolean = false,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object NavigateBack : Event
    data class RequestPermission(val permission: PermissionType) : Event
    data class OpenPermissionSettings(val permission: PermissionType) : Event
    data object RefreshPermissions : Event
  }
}