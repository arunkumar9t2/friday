package dev.arunkumar.jarvis.ui.screens.permissions

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.data.permissions.PermissionManager
import dev.arunkumar.jarvis.data.permissions.PermissionRequestHandler

/** Presenter for FeatureGroupDetailScreen */
class FeatureGroupDetailPresenter @AssistedInject constructor(
  @Assisted private val screen: FeatureGroupDetailScreen,
  @Assisted private val navigator: Navigator,
  private val permissionManager: PermissionManager,
  private val permissionRequestHandler: PermissionRequestHandler
) : Presenter<FeatureGroupDetailScreen.State> {

  @Composable
  override fun present(): FeatureGroupDetailScreen.State {
    val appPermissionState by permissionManager.permissionState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    // Get permissions for this feature group
    val permissions = appPermissionState.allPermissions.filter { permissionState ->
      permissionState.permission.featureGroup == screen.featureGroup
    }

    // Refresh permissions when screen loads
    LaunchedEffect(Unit) {
      permissionManager.refreshPermissionState()
    }

    return FeatureGroupDetailScreen.State(
      featureGroup = screen.featureGroup,
      permissions = permissions,
      isLoading = isLoading,
      eventSink = { event ->
        when (event) {
          is FeatureGroupDetailScreen.Event.NavigateBack -> {
            navigator.pop()
          }

          is FeatureGroupDetailScreen.Event.RefreshPermissions -> {
            permissionManager.refreshPermissionState()
          }

          is FeatureGroupDetailScreen.Event.RequestPermission -> {
            activity?.let { act ->
              isLoading = true
              permissionRequestHandler.requestRuntimePermission(
                activity = act,
                permission = event.permission
              ) { granted ->
                isLoading = false
                permissionManager.refreshPermissionState()
              }
            }
          }

          is FeatureGroupDetailScreen.Event.OpenPermissionSettings -> {
            activity?.let { act ->
              permissionRequestHandler.requestSpecialPermission(
                activity = act,
                permission = event.permission
              ) {
                // Refresh permission state when user returns
                permissionManager.refreshPermissionState()
              }
            }
          }
        }
      }
    )
  }

  @CircuitInject(FeatureGroupDetailScreen::class, ActivityComponent::class)
  @AssistedFactory
  interface Factory {
    fun create(
      screen: FeatureGroupDetailScreen,
      navigator: Navigator
    ): FeatureGroupDetailPresenter
  }
}