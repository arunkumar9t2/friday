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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import dev.arunkumar.jarvis.data.permissions.FeatureGroupState
import dev.arunkumar.jarvis.data.permissions.FeatureStatus

/** UI for PermissionsOverviewScreen */
@CircuitInject(PermissionsOverviewScreen::class, ActivityComponent::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsOverviewUi(
  state: PermissionsOverviewScreen.State,
  modifier: Modifier = Modifier
) {
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = when (state.launchMode) {
              PermissionsOverviewScreen.LaunchMode.SETTINGS -> "Permissions"
              PermissionsOverviewScreen.LaunchMode.ONBOARDING -> "Setup Permissions"
            }
          )
        },
        navigationIcon = {
          if (state.launchMode == PermissionsOverviewScreen.LaunchMode.SETTINGS) {
            IconButton(onClick = { state.eventSink(PermissionsOverviewScreen.Event.NavigateBack) }) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          }
        },
        actions = {
          IconButton(onClick = { state.eventSink(PermissionsOverviewScreen.Event.RefreshPermissions) }) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh permissions")
          }
        }
      )
    },
  ) { paddingValues ->
    // Feature groups list
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(state.appPermissionState.featureGroups.sortedBy { it.group.priority }) { featureGroup ->
        FeatureGroupCard(
          featureGroupState = featureGroup,
          onClick = {
            state.eventSink(
              PermissionsOverviewScreen.Event.NavigateToFeatureGroup(
                featureGroup.group
              )
            )
          }
        )
      }
    }
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureGroupCard(
  featureGroupState: FeatureGroupState,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    onClick = onClick,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Feature icon
      Icon(
        imageVector = featureGroupState.group.icon,
        contentDescription = null,
        modifier = Modifier.size(32.dp),
        tint = MaterialTheme.colorScheme.primary
      )

      Spacer(modifier = Modifier.width(16.dp))

      // Content
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = featureGroupState.group.displayName,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Medium
        )

        Text(
          text = featureGroupState.group.description,
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "${featureGroupState.grantedCount}/${featureGroupState.totalCount} permissions granted",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Status indicator
      Icon(
        imageVector = when (featureGroupState.overallStatus) {
          FeatureStatus.COMPLETE -> Icons.Default.Check
          FeatureStatus.PARTIAL -> Icons.Default.Warning
          FeatureStatus.INCOMPLETE -> Icons.Default.PlayArrow
        },
        contentDescription = null,
        tint = when (featureGroupState.overallStatus) {
          FeatureStatus.COMPLETE -> MaterialTheme.colorScheme.primary
          FeatureStatus.PARTIAL -> MaterialTheme.colorScheme.tertiary
          FeatureStatus.INCOMPLETE -> MaterialTheme.colorScheme.error
        }
      )
    }
  }
}
