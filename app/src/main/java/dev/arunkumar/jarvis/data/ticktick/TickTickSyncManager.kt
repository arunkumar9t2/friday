package dev.arunkumar.jarvis.data.ticktick

import javax.inject.Inject
import javax.inject.Singleton

sealed class SyncResult {
  data object Success : SyncResult()
  data class Error(val exception: Throwable) : SyncResult()
}

@Singleton
class TickTickSyncManager @Inject constructor(
  private val api: TickTickApi,
  private val database: TickTickDatabase
) {
  suspend fun sync(): SyncResult {
    val syncTime = System.currentTimeMillis()
    return try {
      val tasks = api.getTasks()
      val projects = api.getProjects()
      database.replaceAllData(
        tasks.map { it.toEntity(syncTime) },
        projects.map { it.toEntity(syncTime) }
      )
      SyncResult.Success
    } catch (e: Exception) {
      SyncResult.Error(e)
    }
  }
}
