package dev.arunkumar.jarvis.ui.screens.tasks

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.arunkumar.jarvis.ui.state.ListState

@Stable
@Immutable
data class TaskDisplayItem(
  val taskId: Long,
  val projectId: Long,
  val title: String,
  val dueDate: Long,
  val formattedDate: String,
  val priority: Int,
  val projectName: String,
  val priorityColor: Int,
)

@Stable
@Immutable
data class TasksState(
  val tasksState: ListState<TaskDisplayItem>,
  val eventSink: (TasksEvent) -> Unit,
) : CircuitUiState

sealed interface TasksEvent : CircuitUiEvent {
  data object OnRefresh : TasksEvent
  data object OnNavigateBack : TasksEvent
  data class OnTaskClick(val taskId: Long, val projectId: Long) : TasksEvent
}
