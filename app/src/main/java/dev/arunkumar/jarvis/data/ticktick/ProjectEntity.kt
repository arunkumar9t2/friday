package dev.arunkumar.jarvis.data.ticktick

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ticktick_projects")
data class ProjectEntity(
  @PrimaryKey val id: String,
  val name: String,
  val color: String = "#FFFFFF",
  val sortOrder: Long = 0,
  val lastSyncedAt: Long = System.currentTimeMillis()
)
