package dev.arunkumar.jarvis.ui.screens

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

/**
 * Sealed interface representing all the screens in the app. Each screen is
 * a data class that can hold navigation arguments.
 */
sealed interface AppScreen : Screen, Parcelable

@Parcelize
@Immutable
data class HomeScreen(val id: String = "") : AppScreen {
  override fun toString() = "HomeScreen"

  @Stable
  @Immutable
  data class State(
    val title: String,
    val counter: Int,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object OnIncrementClicked : Event
    data object OnNavigateToDetails : Event
    data object OnNavigateToSettings : Event
    data object OnNavigateToTasks : Event
  }
}

@Parcelize
@Immutable
data class DetailsScreen(
  val itemId: String,
  val title: String
) : AppScreen {
  override fun toString() = "DetailsScreen($itemId, $title)"

  @Stable
  @Immutable
  data class State(
    val itemId: String,
    val title: String,
    val isFavorite: Boolean,
    val likeCount: Int,
    val description: String,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object OnBackClicked : Event
    data object OnFavoriteToggled : Event
    data object OnLikeClicked : Event
  }
}

@Parcelize
@Immutable
data class SettingsScreen(val id: String = "") : AppScreen {
  override fun toString() = "SettingsScreen"

  @Stable
  @Immutable
  data class State(
    val isNotificationsEnabled: Boolean,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object OnBackClicked : Event
    data object OnNotificationsToggled : Event
    data object OnPermissionsClicked : Event
  }
}

@Parcelize
@Immutable
data class TasksScreen(val id: String = "") : AppScreen {
  override fun toString() = "TasksScreen"
}

@Parcelize
@Immutable
data class ProfileScreen(
  val userId: String
) : AppScreen {
  override fun toString() = "ProfileScreen($userId)"

  @Stable
  @Immutable
  data class State(
    val userId: String,
    val username: String,
    val email: String,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object OnBackClicked : Event
    data object OnEditProfile : Event
    data object OnLogout : Event
  }
}
