package dev.arunkumar.jarvis.data.ticktick

import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Parses an ISO 8601 date string to epoch milliseconds.
 * Supports formats:
 * - "2024-01-15T10:30:00.000Z" (standard ISO 8601 with Z)
 * - "2024-01-15T10:30:00.000+0000" (with timezone offset)
 */
fun String?.toEpochMillis(): Long? {
  if (this == null) return null
  return try {
    Instant.parse(this).toEpochMilli()
  } catch (e: Exception) {
    try {
      // Handle format without Z: "2024-01-15T10:30:00.000+0000"
      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
      ZonedDateTime.parse(this, formatter).toInstant().toEpochMilli()
    } catch (e: Exception) {
      null
    }
  }
}

// API -> Entity mappers

fun ApiTickTickTask.toEntity(syncedAt: Long = System.currentTimeMillis()) = TaskEntity(
  id = id,
  projectId = projectId,
  title = title,
  content = content,
  priority = priority,
  dueDateEpochMs = dueDate.toEpochMillis(),
  isAllDay = isAllDay,
  sortOrder = sortOrder,
  completedTimeEpochMs = completedTime.toEpochMillis(),
  lastSyncedAt = syncedAt
)

fun ApiTickTickProject.toEntity(syncedAt: Long = System.currentTimeMillis()) = ProjectEntity(
  id = id,
  name = name,
  color = color ?: "#FFFFFF",
  sortOrder = sortOrder,
  lastSyncedAt = syncedAt
)

// Entity -> Domain mappers

fun TaskEntity.toDomain() = TickTickTask(
  id = id,
  projectId = projectId,
  title = title,
  dueDate = dueDateEpochMs ?: 0L,
  sortOrder = sortOrder,
  completedTime = completedTimeEpochMs ?: 0L,
  priority = priority,
  reminderTime = 0L, // Not available from API
  repeat = false     // Not available from API
)

fun ProjectEntity.toDomain() = TickTickProject(
  id = id,
  name = name,
  color = color
)

// List mappers for convenience

fun List<ApiTickTickTask>.toEntities(syncedAt: Long = System.currentTimeMillis()) =
  map { it.toEntity(syncedAt) }

fun List<ApiTickTickProject>.toProjectEntities(syncedAt: Long = System.currentTimeMillis()) =
  map { it.toEntity(syncedAt) }

fun List<TaskEntity>.toDomainTasks() = map { it.toDomain() }

fun List<ProjectEntity>.toDomainProjects() = map { it.toDomain() }
