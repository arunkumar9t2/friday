package dev.arunkumar.jarvis.ui.screens.permissions

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.data.permissions.PermissionManager
import dev.arunkumar.jarvis.data.permissions.PermissionRequestHandler

/** Presenter for PermissionsOverviewScreen */
class PermissionsOverviewPresenter @AssistedInject constructor(
  @Assisted private val screen: PermissionsOverviewScreen,
  @Assisted private val navigator: Navigator,
  private val permissionManager: PermissionManager,
  private val permissionRequestHandler: PermissionRequestHandler
) : Presenter<PermissionsOverviewScreen.State> {

  @Composable
  override fun present(): PermissionsOverviewScreen.State {
    val appPermissionState by permissionManager.permissionState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val lifecycleOwner = LocalLifecycleOwner.current

    // Refresh permissions when screen loads
    LaunchedEffect(Unit) {
      permissionManager.refreshPermissionState()
    }

    // Refresh permissions when returning from settings
    DisposableEffect(lifecycleOwner) {
      val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
          permissionManager.refreshPermissionState()
        }
      }
      lifecycleOwner.lifecycle.addObserver(observer)
      onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
      }
    }

    return PermissionsOverviewScreen.State(
      appPermissionState = appPermissionState,
      launchMode = screen.launchMode,
      isLoading = isLoading,
      eventSink = { event ->
        when (event) {
          is PermissionsOverviewScreen.Event.RefreshPermissions -> {
            isLoading = true
            permissionManager.refreshPermissionState()
            isLoading = false
          }

          is PermissionsOverviewScreen.Event.NavigateBack -> {
            navigator.pop()
          }

          is PermissionsOverviewScreen.Event.NavigateToFeatureGroup -> {
            // TODO: Navigate to feature group detail screen
          }

          is PermissionsOverviewScreen.Event.RequestPermission -> {
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

          is PermissionsOverviewScreen.Event.OpenPermissionSettings -> {
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

          is PermissionsOverviewScreen.Event.RequestAllMissingPermissions -> {
            activity?.let { act ->
              val missingPermissions = permissionManager.getRuntimePermissionsNeedingAction()
              if (missingPermissions.isNotEmpty()) {
                isLoading = true
                permissionRequestHandler.requestRuntimePermissions(
                  activity = act,
                  permissions = missingPermissions.map { it.permission }
                ) { results ->
                  isLoading = false
                  permissionManager.refreshPermissionState()
                }
              }
            }
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
