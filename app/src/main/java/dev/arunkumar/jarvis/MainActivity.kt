package dev.arunkumar.jarvis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import dagger.hilt.android.AndroidEntryPoint
import dev.arunkumar.jarvis.data.permissions.PermissionLauncherManager
import dev.arunkumar.jarvis.data.permissions.PermissionManager
import dev.arunkumar.jarvis.service.TickTickService.Companion.startTickTickService
import dev.arunkumar.jarvis.ui.screens.HomeScreen
import dev.arunkumar.jarvis.ui.theme.AppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var circuit: Circuit

  @Inject
  lateinit var permissionLauncherManager: PermissionLauncherManager

  @Inject
  lateinit var permissionManager: PermissionManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize permission launcher before activity reaches STARTED state
    permissionLauncherManager.initialize(this)
    
    startTickTickService()

    enableEdgeToEdge()
    setContent {
      AppTheme {
        JarvisApp(
          circuit = circuit,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }

  override fun onResume() {
    super.onResume()
    // Refresh permission state when returning from settings
    // This is critical for sensitive permissions that don't have callbacks
    permissionManager.refreshPermissionState()
  }
}

@Composable
fun JarvisApp(
  circuit: Circuit,
  modifier: Modifier = Modifier
) {
  CircuitCompositionLocals(circuit) {
    val backStack = rememberSaveableBackStack(root = HomeScreen())
    val navigator = rememberCircuitNavigator(backStack)

    NavigableCircuitContent(
      navigator = navigator,
      backStack = backStack,
      modifier = modifier
    )
  }
}
