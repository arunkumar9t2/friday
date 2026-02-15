package dev.arunkumar.jarvis.ui.screens.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.data.permissions.FeatureGroup
import dev.arunkumar.jarvis.data.permissions.PermissionState
import dev.arunkumar.jarvis.data.permissions.PermissionStatus
import dev.arunkumar.jarvis.data.permissions.ProtectionLevel
import dev.arunkumar.jarvis.ui.state.ListState

/** UI for FeatureGroupDetailScreen */
@CircuitInject(FeatureGroupDetailScreen::class, ActivityComponent::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureGroupDetailUi(
  state: FeatureGroupDetailScreen.State,
  modifier: Modifier = Modifier
) {
  val snackbarHostState = remember { SnackbarHostState() }

  // Show message as snackbar
  state.message?.let { message ->
    LaunchedEffect(message) {
      try {
        snackbarHostState.showSnackbar(
          message = message.text,
          duration = SnackbarDuration.Short
        )
      } finally {
        state.eventSink(FeatureGroupDetailScreen.Event.OnMessageDismissed)
      }
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    topBar = {
      TopAppBar(
        title = { Text(text = state.featureGroup.displayName) },
        navigationIcon = {
          IconButton(onClick = { state.eventSink(FeatureGroupDetailScreen.Event.OnNavigateBack) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { state.eventSink(FeatureGroupDetailScreen.Event.OnRefresh) }) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh permissions")
          }
        }
      )
    }
  ) { paddingValues ->
    when (val permissionsState = state.permissionsState) {
      is ListState.Loading -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator()
        }
      }

      is ListState.Empty -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No permissions in this group",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      is ListState.Loaded -> {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Feature group header
          item {
            FeatureGroupHeader(featureGroup = state.featureGroup)
          }

          // Individual permissions
          items(permissionsState.items, key = { it.permission.permission }) { permission ->
            PermissionCard(
              permissionState = permission,
              onRequestPermission = {
                state.eventSink(FeatureGroupDetailScreen.Event.OnRequestPermission(permission.permission))
              },
              onOpenSettings = {
                state.eventSink(FeatureGroupDetailScreen.Event.OnOpenSettings(permission.permission))
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun FeatureGroupHeader(
  featureGroup: FeatureGroup,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = featureGroup.icon,
          contentDescription = null,
          modifier = Modifier.size(32.dp),
          tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
          text = featureGroup.displayName,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = featureGroup.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun PermissionCard(
  permissionState: PermissionState,
  onRequestPermission: () -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = permissionState.permission.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
          )

          Text(
            text = permissionState.permission.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Status indicator
        if (permissionState.status == PermissionStatus.GRANTED) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Granted",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action buttons
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        when {
          permissionState.status == PermissionStatus.GRANTED -> {
            // Already granted - show settings button for special permissions
            if (permissionState.permission.protectionLevel != ProtectionLevel.DANGEROUS) {
              OutlinedButton(onClick = onOpenSettings) {
                Icon(
                  imageVector = Icons.Default.Settings,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Settings")
              }
            }
          }

          // Permanently denied dangerous permission → redirect to app settings
          permissionState.isPermanentlyDenied &&
            permissionState.permission.protectionLevel == ProtectionLevel.DANGEROUS -> {
            Button(onClick = onOpenSettings) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(stringResource(dev.arunkumar.jarvis.R.string.permission_open_app_settings))
            }
          }

          permissionState.permission.protectionLevel == ProtectionLevel.DANGEROUS -> {
            // Runtime permission - show grant button
            Button(onClick = onRequestPermission) {
              Text("Grant Permission")
            }
          }

          else -> {
            // Special permission - show settings button
            Button(onClick = onOpenSettings) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Open Settings")
            }
          }
        }
      }
    }
  }
}
