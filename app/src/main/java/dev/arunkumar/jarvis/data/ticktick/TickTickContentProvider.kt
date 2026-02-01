package dev.arunkumar.jarvis.data.ticktick

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.arunkumar.jarvis.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TickTickContentProvider @Inject constructor(
  @ApplicationContext private val context: Context,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

  suspend fun getAllProjects(): List<TickTickProject> = withContext(ioDispatcher) {
    val resolver = context.contentResolver
    resolver.query(PROJECT_URI, null, null, null, null)?.use { cursor ->
      buildList {
        if (cursor.moveToFirst()) {
          do {
            add(cursorToProject(cursor))
          } while (cursor.moveToNext())
        }
      }
    } ?: emptyList()
  }

  suspend fun getPendingTasks(): List<TickTickTask> = withContext(ioDispatcher) {
    getAllTasks()
      .asSequence()
      .filter { it.completedTime == 0L }
      .filter { it.dueDate != Instant.EPOCH.toEpochMilli() }
      .sortedBy { Instant.ofEpochMilli(it.dueDate) }
      .toList()
  }

  suspend fun getAllTasks(
    projection: Array<String> = TaskColumns.entries.map { it.name }.toTypedArray(),
    selection: String? = null,
    selectionArgs: Array<String> = arrayOf("-1", "true"),
    sortOrder: String? = null,
  ): List<TickTickTask> = withContext(ioDispatcher) {
    val resolver = context.contentResolver
    resolver.query(TASK_URI, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
      buildList {
        if (cursor.moveToFirst()) {
          do {
            add(cursorToTask(cursor))
          } while (cursor.moveToNext())
        }
      }
    } ?: emptyList()
  }

  fun insertTaskIntent(projectId: Long): Intent {
    return Intent(Intent.ACTION_INSERT).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      setDataAndType(
        TASK_URI.buildUpon().appendEncodedPath(projectId.toString()).build(),
        TASK_CONTENT_ITEM_TYPE,
      )
    }
  }

  private fun cursorToTask(cursor: Cursor): TickTickTask {
    return TickTickTask(
      id = cursor.getLong(TaskColumns.ID.ordinal),
      projectId = cursor.getLong(TaskColumns.LIST_ID.ordinal),
      title = cursor.getString(TaskColumns.TITLE.ordinal),
      dueDate = cursor.getLong(TaskColumns.DUEDATE.ordinal),
      sortOrder = cursor.getLong(TaskColumns.SORT_ORDER.ordinal),
      completedTime = cursor.getLong(TaskColumns.COMPLETED.ordinal),
      priority = cursor.getInt(TaskColumns.PRIORITY.ordinal),
      reminderTime = cursor.getLong(TaskColumns.REMINDER_TIME.ordinal),
      repeat = cursor.getInt(TaskColumns.REPEAT_FLAG.ordinal) != 0,
    )
  }

  private fun cursorToProject(cursor: Cursor): TickTickProject {
    return TickTickProject(
      id = cursor.getLong(ProjectColumns.ID.ordinal),
      name = cursor.getString(ProjectColumns.NAME.ordinal),
      color = cursor.getString(ProjectColumns.COLOR.ordinal),
    )
  }

  private enum class TaskColumns {
    ID, LIST_ID, TITLE, DUEDATE, SORT_ORDER, COMPLETED, PRIORITY, REMINDER_TIME, REPEAT_FLAG
  }

  private enum class ProjectColumns {
    ID, NAME, COLOR
  }

  companion object {
    val TASK_URI: Uri = Uri.parse("content://com.ticktick.task.data/tasks")
    private val PROJECT_URI: Uri = Uri.parse("content://com.ticktick.task.data/tasklist")
    private const val TASK_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/ticktick.task.task"
    const val TICKTICK_PACKAGE = "com.ticktick.task"

    fun viewTaskIntent(projectId: Long, taskId: Long): Intent {
      return Intent(Intent.ACTION_VIEW).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        putExtra("tasklist_id", projectId)
        setDataAndType(ContentUris.withAppendedId(TASK_URI, taskId), TASK_CONTENT_ITEM_TYPE)
      }
    }
  }
}
