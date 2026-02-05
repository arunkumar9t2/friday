package dev.arunkumar.jarvis.data.ticktick

import android.content.Intent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TickTickRepository @Inject constructor(
  private val taskDao: TaskDao,
  private val projectDao: ProjectDao,
  private val syncManager: TickTickSyncManager,
) {

  suspend fun refresh(): SyncResult = syncManager.sync()

  suspend fun getPendingTasks(): List<TickTickTask> =
    taskDao.getPendingTasks().map { it.toDomain() }

  suspend fun getAllProjects(): List<TickTickProject> =
    projectDao.getAllProjects().map { it.toDomain() }

  suspend fun getNotificationItems(limit: Int = 6): List<TickTickNotificationItem> {
    val projectsById = getAllProjects().associateBy(TickTickProject::id)
    val rawTasks = getPendingTasks().take(limit)

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

  fun getInsertTaskIntent(projectId: String): Intent =
    TickTickIntents.insertTaskIntent(projectId)

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
