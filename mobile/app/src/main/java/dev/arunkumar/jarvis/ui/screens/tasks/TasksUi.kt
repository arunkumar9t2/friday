package dev.arunkumar.jarvis.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.R
import dev.arunkumar.jarvis.ui.screens.TasksScreen
import dev.arunkumar.jarvis.ui.state.ListState

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(TasksScreen::class, ActivityComponent::class)
@Composable
fun TasksUi(state: TasksState, modifier: Modifier = Modifier) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.ticktick_tasks)) },
        navigationIcon = {
          IconButton(onClick = { state.eventSink(TasksEvent.OnNavigateBack) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        },
      )
    },
    modifier = modifier,
  ) { paddingValues ->
    when (val tasksState = state.tasksState) {
      is ListState.Loading -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .testTag("tasks_loading"),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }
      }
      is ListState.Empty -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .testTag("tasks_empty"),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = stringResource(R.string.ticktick_no_tasks),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      is ListState.Loaded -> {
        PullToRefreshBox(
          isRefreshing = false,
          onRefresh = { state.eventSink(TasksEvent.OnRefresh) },
          modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        ) {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .testTag("tasks_list"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            items(
              items = tasksState.items,
              key = { it.taskId },
            ) { task ->
              TaskCard(
                task = task,
                onClick = {
                  state.eventSink(TasksEvent.OnTaskClick(task.taskId, task.projectId))
                },
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TaskCard(
  task: TaskDisplayItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("task_card_${task.taskId}")
      .clickable(onClick = onClick),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Row(
      modifier = Modifier.height(64.dp),
    ) {
      Box(
        modifier = Modifier
          .width(4.dp)
          .fillMaxHeight()
          .background(Color(task.priorityColor)),
      )
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
      ) {
        Text(
          text = task.title,
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = task.formattedDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          if (task.projectName.isNotEmpty()) {
            Text(
              text = task.projectName,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
    }
  }
}
