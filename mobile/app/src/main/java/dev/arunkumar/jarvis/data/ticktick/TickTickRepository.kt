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

  fun getInsertTaskIntent(projectId: String): Intent =
    TickTickIntents.insertTaskIntent(projectId)
}
