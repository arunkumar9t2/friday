package dev.arunkumar.jarvis.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.ui.screens.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(SettingsScreen::class, ActivityComponent::class)
@Composable
fun SettingsUi(state: SettingsScreen.State, modifier: Modifier = Modifier) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Settings") },
        navigationIcon = {
          IconButton(onClick = { state.eventSink(SettingsScreen.Event.OnBackClicked) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        }
      )
    },
    modifier = modifier
  ) { paddingValues ->
    Column(
      modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(16.dp)
          .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // Notifications Section
      SettingsSection(title = "Notifications") {
        SettingsItem(
          icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
          title = "Enable Notifications",
          subtitle = "Receive notifications for important updates",
          trailing = {
            Switch(
              checked = state.isNotificationsEnabled,
              onCheckedChange = { state.eventSink(SettingsScreen.Event.OnNotificationsToggled) }
            )
          }
        )
      }

      // Permissions Section
      SettingsSection(title = "Permissions") {
        SettingsItem(
          icon = { Icon(Icons.Filled.Lock, contentDescription = null) },
          title = "App Permissions",
          subtitle = "Manage permissions for assistant features",
          trailing = {
            IconButton(onClick = { state.eventSink(SettingsScreen.Event.OnPermissionsClicked) }) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Open")
            }
          }
        )
      }


      // About Section
      SettingsSection(title = "About") {
        Column {
          Text(
            text = "Jarvis AI Assistant",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Built with Circuit navigation and Hilt dependency injection. Demonstrating modern Android development practices with Jetpack Compose.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun SettingsSection(
  title: String,
  content: @Composable () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(12.dp))
      content()
    }
  }
}

@Composable
private fun SettingsItem(
  icon: @Composable () -> Unit,
  title: String,
  subtitle: String,
  trailing: @Composable () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    icon()
    Spacer(modifier = Modifier.width(16.dp))
    Column(
      modifier = Modifier.weight(1f)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
    Spacer(modifier = Modifier.width(16.dp))
    trailing()
  }
}
