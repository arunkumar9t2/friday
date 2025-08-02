package dev.arunkumar.jarvis.ui.screens.permissions

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.arunkumar.jarvis.data.permissions.AppPermissionState
import dev.arunkumar.jarvis.data.permissions.FeatureGroup
import dev.arunkumar.jarvis.data.permissions.PermissionType
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

  @Stable
  @Immutable
  data class State(
    val appPermissionState: AppPermissionState,
    val launchMode: LaunchMode,
    val isLoading: Boolean = false,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object RefreshPermissions : Event
    data object NavigateBack : Event
    data class NavigateToFeatureGroup(val group: FeatureGroup) : Event
    data class RequestPermission(val permission: PermissionType) : Event
    data class OpenPermissionSettings(val permission: PermissionType) : Event
    data object RequestAllMissingPermissions : Event
  }
}
