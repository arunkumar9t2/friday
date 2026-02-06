package dev.arunkumar.jarvis.data.ticktick

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "ticktick_tasks",
  indices = [Index("projectId")]
)
data class TaskEntity(
  @PrimaryKey val id: String,
  val projectId: String,
  val title: String,
  val content: String? = null,
  val priority: Int = 0,
  val dueDateEpochMs: Long? = null,
  val isAllDay: Boolean = false,
  val sortOrder: Long = 0,
  val completedTimeEpochMs: Long? = null,
  val lastSyncedAt: Long = System.currentTimeMillis()
)
