package dev.arunkumar.jarvis.ui.screens.permissions

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.data.permissions.PermissionState
import dev.arunkumar.jarvis.data.permissions.PermissionStatus
import dev.arunkumar.jarvis.data.permissions.ProtectionLevel

/** UI for FeatureGroupDetailScreen */
@CircuitInject(FeatureGroupDetailScreen::class, ActivityComponent::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureGroupDetailUi(
  state: FeatureGroupDetailScreen.State,
  modifier: Modifier = Modifier
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = state.featureGroup.displayName) },
        navigationIcon = {
          IconButton(onClick = { state.eventSink(FeatureGroupDetailScreen.Event.NavigateBack) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = { state.eventSink(FeatureGroupDetailScreen.Event.RefreshPermissions) }) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh permissions")
          }
        }
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Feature group header
      item {
        Card(
          modifier = Modifier.fillMaxWidth()
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
                imageVector = state.featureGroup.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
              )
              
              Spacer(modifier = Modifier.width(16.dp))
              
              Text(
                text = state.featureGroup.displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
              )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
              text = state.featureGroup.description,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Individual permissions
      items(state.permissions) { permission ->
        PermissionCard(
          permissionState = permission,
          isLoading = state.isLoading,
          onRequestPermission = { 
            state.eventSink(FeatureGroupDetailScreen.Event.RequestPermission(permission.permission))
          },
          onOpenSettings = {
            state.eventSink(FeatureGroupDetailScreen.Event.OpenPermissionSettings(permission.permission))
          }
        )
      }
    }
  }
}

@Composable
private fun PermissionCard(
  permissionState: PermissionState,
  isLoading: Boolean,
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
              OutlinedButton(
                onClick = onOpenSettings,
                enabled = !isLoading
              ) {
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
          
          permissionState.permission.protectionLevel == ProtectionLevel.DANGEROUS -> {
            // Runtime permission - show grant button
            Button(
              onClick = onRequestPermission,
              enabled = !isLoading
            ) {
              Text("Grant Permission")
            }
          }
          
          else -> {
            // Special permission - show settings button
            Button(
              onClick = onOpenSettings,
              enabled = !isLoading
            ) {
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