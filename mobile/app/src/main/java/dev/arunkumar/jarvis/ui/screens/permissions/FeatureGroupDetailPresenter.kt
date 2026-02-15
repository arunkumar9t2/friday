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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.data.permissions.PermissionManager
import dev.arunkumar.jarvis.data.permissions.PermissionRequestHandler
import dev.arunkumar.jarvis.data.permissions.PermissionState
import dev.arunkumar.jarvis.ui.state.ListState

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
    var permissionsState by remember { mutableStateOf<ListState<PermissionState>>(ListState.Loading) }
    var message by remember { mutableStateOf<FeatureGroupDetailScreen.Message?>(null) }
    var isReturningFromSettings by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    // Refresh permissions on initial composition
    LaunchedEffect(Unit) {
      permissionManager.refreshPermissionState()
    }

    // Refresh permissions when returning from settings (not on every resume)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner.lifecycle, screen.featureGroup) {
      val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME && isReturningFromSettings) {
          permissionManager.refreshPermissionState()
          isReturningFromSettings = false
        }
      }
      lifecycleOwner.lifecycle.addObserver(observer)
      onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Update permissionsState when appPermissionState changes
    LaunchedEffect(appPermissionState) {
      val permissions = appPermissionState.allPermissions.filter { permissionState ->
        permissionState.permission.featureGroup == screen.featureGroup
      }
      permissionsState = if (permissions.isEmpty()) {
        ListState.Empty
      } else {
        ListState.Loaded(permissions)
      }
    }

    return FeatureGroupDetailScreen.State(
      featureGroup = screen.featureGroup,
      permissionsState = permissionsState,
      message = message,
      eventSink = { event ->
        when (event) {
          is FeatureGroupDetailScreen.Event.OnNavigateBack -> {
            navigator.pop()
          }

          is FeatureGroupDetailScreen.Event.OnRefresh -> {
            permissionsState = ListState.Loading
            permissionManager.refreshPermissionState()
          }

          is FeatureGroupDetailScreen.Event.OnRequestPermission -> {
            activity?.let { act ->
              permissionsState = ListState.Loading
              permissionRequestHandler.requestRuntimePermission(
                activity = act,
                permission = event.permission
              ) { granted ->
                message = if (granted) {
                  FeatureGroupDetailScreen.Message.Success("Permission granted")
                } else {
                  FeatureGroupDetailScreen.Message.Error("Permission denied")
                }
              }
            }
          }

          is FeatureGroupDetailScreen.Event.OnOpenSettings -> {
            activity?.let { act ->
              val permission = event.permission
              isReturningFromSettings = true
              if (permission.protectionLevel == dev.arunkumar.jarvis.data.permissions.ProtectionLevel.DANGEROUS) {
                permissionRequestHandler.openAppSettings(activity = act)
              } else {
                permissionRequestHandler.requestSpecialPermission(
                  activity = act,
                  permission = permission
                )
              }
            }
          }

          is FeatureGroupDetailScreen.Event.OnMessageDismissed -> {
            message = null
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
