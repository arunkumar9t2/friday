package dev.arunkumar.jarvis.ui.screens.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.data.permissions.PermissionManager
import dev.arunkumar.jarvis.ui.state.ListState

/** Presenter for PermissionsOverviewScreen */
class PermissionsOverviewPresenter @AssistedInject constructor(
  @Assisted private val screen: PermissionsOverviewScreen,
  @Assisted private val navigator: Navigator,
  private val permissionManager: PermissionManager
) : Presenter<PermissionsOverviewScreen.State> {

  @Composable
  override fun present(): PermissionsOverviewScreen.State {
    val appPermissionState by permissionManager.permissionState.collectAsState()
    var featureGroupsState by remember { mutableStateOf<ListState<dev.arunkumar.jarvis.data.permissions.FeatureGroupState>>(ListState.Loading) }
    var message by remember { mutableStateOf<PermissionsOverviewScreen.Message?>(null) }

    // Refresh permissions when screen loads and update state
    LaunchedEffect(Unit) {
      permissionManager.refreshPermissionState()
    }

    // Update featureGroupsState when appPermissionState changes
    LaunchedEffect(appPermissionState) {
      val groups = appPermissionState.featureGroups.sortedBy { it.group.priority }
      featureGroupsState = if (groups.isEmpty()) {
        ListState.Empty
      } else {
        ListState.Loaded(groups)
      }
    }

    return PermissionsOverviewScreen.State(
      featureGroupsState = featureGroupsState,
      launchMode = screen.launchMode,
      message = message,
      eventSink = { event ->
        when (event) {
          is PermissionsOverviewScreen.Event.OnRefresh -> {
            featureGroupsState = ListState.Loading
            permissionManager.refreshPermissionState()
          }

          is PermissionsOverviewScreen.Event.OnNavigateBack -> {
            navigator.pop()
          }

          is PermissionsOverviewScreen.Event.OnFeatureGroupClick -> {
            navigator.goTo(FeatureGroupDetailScreen(featureGroup = event.group))
          }

          is PermissionsOverviewScreen.Event.OnMessageDismissed -> {
            message = null
          }
        }
      }
    )
  }

  @CircuitInject(PermissionsOverviewScreen::class, ActivityComponent::class)
  @AssistedFactory
  interface Factory {
    fun create(
      screen: PermissionsOverviewScreen,
      navigator: Navigator
    ): PermissionsOverviewPresenter
  }
}
