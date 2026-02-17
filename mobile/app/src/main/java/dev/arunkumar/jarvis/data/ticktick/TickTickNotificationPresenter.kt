package dev.arunkumar.jarvis.data.ticktick

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TickTickNotificationPresenter @Inject constructor(
  private val taskDao: TaskDao,
  private val projectDao: ProjectDao,
) {

  fun notificationItems(limit: Int = 6): Flow<List<TickTickNotificationItem>> {
    return combine(
      taskDao.getPendingTasksFlow(),
      projectDao.getAllProjectsFlow(),
    ) { taskEntities, projectEntities ->
      val projectsById = projectEntities
        .map { it.toDomain() }
        .associateBy(TickTickProject::id)
      val rawTasks = taskEntities.map { it.toDomain() }.take(limit)
      buildNotificationItems(rawTasks, projectsById)
    }
  }

  private fun buildNotificationItems(
    rawTasks: List<TickTickTask>,
    projectsById: Map<String, TickTickProject>,
  ): List<TickTickNotificationItem> = buildList {
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

  private fun mapToNotificationItem(
    task: TickTickTask,
    projectsById: Map<String, TickTickProject>,
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
