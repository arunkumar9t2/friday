package dev.arunkumar.jarvis.data.ticktick

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TickTickSync"

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
      val response = api.getTasks()
      if (response.warnings.isNotEmpty()) {
        Log.w(TAG, "Sync warnings: ${response.warnings}")
      }
      database.replaceAllData(
        response.tasks.map { it.toEntity(syncTime) },
        response.projects.map { it.toEntity(syncTime) }
      )
      SyncResult.Success
    } catch (e: Exception) {
      SyncResult.Error(e)
    }
  }
}
