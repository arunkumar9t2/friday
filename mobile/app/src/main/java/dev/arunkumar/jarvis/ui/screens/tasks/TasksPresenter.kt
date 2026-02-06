package dev.arunkumar.jarvis.ui.screens.tasks

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.arunkumar.jarvis.data.ticktick.TickTickIntents
import dev.arunkumar.jarvis.data.ticktick.TickTickPriority
import dev.arunkumar.jarvis.data.ticktick.TickTickRepository
import dev.arunkumar.jarvis.data.ticktick.formatDueDate
import dev.arunkumar.jarvis.ui.screens.TasksScreen
import dev.arunkumar.jarvis.ui.state.ListState

class TasksPresenter @AssistedInject constructor(
  @Assisted private val screen: TasksScreen,
  @Assisted private val navigator: Navigator,
  private val repository: TickTickRepository,
  @ApplicationContext private val context: Context,
) : Presenter<TasksState> {

  @Composable
  override fun present(): TasksState {
    var tasksState by remember { mutableStateOf<ListState<TaskDisplayItem>>(ListState.Loading) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
      tasksState = ListState.Loading
      try {
        val projectsById = repository.getAllProjects().associateBy { it.id }
        val tasks = repository.getPendingTasks().map { task ->
          val project = projectsById[task.projectId]
          TaskDisplayItem(
            taskId = task.id,
            projectId = task.projectId,
            title = task.title,
            dueDate = task.dueDate,
            formattedDate = task.dueDate.formatDueDate(context),
            priority = task.priority,
            projectName = project?.name ?: "",
            priorityColor = TickTickPriority.fromLevel(task.priority).color,
          )
        }
        tasksState = if (tasks.isEmpty()) ListState.Empty else ListState.Loaded(tasks)
      } catch (e: Exception) {
        Log.e("TasksPresenter", "Failed to load tasks", e)
        tasksState = ListState.Empty
      }
    }

    return TasksState(
      tasksState = tasksState,
      eventSink = { event ->
        when (event) {
          TasksEvent.OnRefresh -> refreshTrigger++
          TasksEvent.OnNavigateBack -> navigator.pop()
          is TasksEvent.OnTaskClick -> {
            val intent = TickTickIntents.viewTaskIntent(event.projectId, event.taskId)
            context.startActivity(intent)
          }
        }
      },
    )
  }

  @CircuitInject(TasksScreen::class, ActivityComponent::class)
  @AssistedFactory
  interface Factory {
    fun create(
      screen: TasksScreen,
      navigator: Navigator,
    ): TasksPresenter
  }
}
