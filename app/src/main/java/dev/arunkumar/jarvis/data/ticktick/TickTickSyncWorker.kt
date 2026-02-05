package dev.arunkumar.jarvis.data.ticktick

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TickTickSyncWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted params: WorkerParameters,
  private val syncManager: TickTickSyncManager
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return when (syncManager.sync()) {
      is SyncResult.Success -> Result.success()
      is SyncResult.Error -> Result.retry()
    }
  }

  companion object {
    const val WORK_NAME = "ticktick_sync"

    fun enqueueOneTimeSync(workManager: WorkManager) {
      val request = OneTimeWorkRequestBuilder<TickTickSyncWorker>()
        .setConstraints(
          Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        )
        .build()

      workManager.enqueueUniqueWork(
        WORK_NAME,
        ExistingWorkPolicy.REPLACE,
        request
      )
    }
  }
}
