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
import dev.arunkumar.jarvis.ui.screens.HomeScreen
import dev.arunkumar.jarvis.ui.theme.JarvisTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var circuit: Circuit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JarvisTheme {
                JarvisApp(
                    circuit = circuit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
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