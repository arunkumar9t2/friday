package dev.arunkumar.jarvis.data.ticktick

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM ticktick_tasks WHERE (completedTimeEpochMs IS NULL OR completedTimeEpochMs = 0) AND dueDateEpochMs IS NOT NULL ORDER BY dueDateEpochMs ASC")
  fun getPendingTasksFlow(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM ticktick_tasks WHERE (completedTimeEpochMs IS NULL OR completedTimeEpochMs = 0) AND dueDateEpochMs IS NOT NULL ORDER BY dueDateEpochMs ASC")
  suspend fun getPendingTasks(): List<TaskEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(tasks: List<TaskEntity>)

  @Query("DELETE FROM ticktick_tasks")
  suspend fun deleteAll()

  @Transaction
  suspend fun replaceAll(tasks: List<TaskEntity>) {
    deleteAll()
    insertAll(tasks)
  }
}
