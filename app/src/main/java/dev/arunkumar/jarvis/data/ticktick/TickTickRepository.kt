package dev.arunkumar.jarvis.data.ticktick

import android.content.Intent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TickTickRepository @Inject constructor(
  private val contentProvider: TickTickContentProvider,
) {

  suspend fun getPendingTasks(): List<TickTickTask> {
    return contentProvider.getPendingTasks()
  }

  suspend fun getAllProjects(): List<TickTickProject> {
    return contentProvider.getAllProjects()
  }

  suspend fun getNotificationItems(limit: Int = 6): List<TickTickNotificationItem> {
    val projectsById = contentProvider.getAllProjects()
      .associateBy(TickTickProject::id)

    val rawTasks = contentProvider.getPendingTasks().take(limit)

    return buildList {
      if (rawTasks.size > 1) {
        add(mapToNotificationItem(rawTasks.first(), projectsById))
        rawTasks.zipWithNext { curr, next ->
          when {
            curr.dueDate.isPreviousDays() && next.dueDate.isTodayOrAfter() -> {
              add(TickTickNotificationItem.TickTickOverdue)
            }
            curr.dueDate.isToday() && next.dueDate.isUpcoming() -> {
              add(TickTickNotificationItem.TodaySeparator)
            }
          }
          add(mapToNotificationItem(next, projectsById))
        }
      } else {
        rawTasks.forEach { add(mapToNotificationItem(it, projectsById)) }
      }
    }
  }

  fun getInsertTaskIntent(projectId: Long): Intent {
    return contentProvider.insertTaskIntent(projectId)
  }

  private fun mapToNotificationItem(
    task: TickTickTask,
    projectsById: Map<Long, TickTickProject>,
  ): TickTickNotificationItem.TickTickTaskItem {
    val project = projectsById[task.projectId]
    return TickTickNotificationItem.TickTickTaskItem(
      taskId = task.id,
      projectId = task.projectId,
      title = task.title,
      dueDate = task.dueDate,
      priority = task.priority,
      projectName = project?.name ?: "",
      color = project?.color ?: "#FFFFFF",
    )
  }
}
