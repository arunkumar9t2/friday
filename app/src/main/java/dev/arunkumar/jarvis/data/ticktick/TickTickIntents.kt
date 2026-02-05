package dev.arunkumar.jarvis.data.ticktick

import android.content.Intent
import android.net.Uri

object TickTickIntents {
  const val TICKTICK_PACKAGE = "com.ticktick.task"
  private const val TASK_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/ticktick.task.task"
  private val TASK_URI: Uri = Uri.parse("content://com.ticktick.task.data/tasks")

  fun viewTaskIntent(projectId: String, taskId: String): Intent {
    return Intent(Intent.ACTION_VIEW).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
      putExtra("tasklist_id", projectId)
      data = Uri.parse("ticktick://task/$taskId")
    }
  }

  fun insertTaskIntent(projectId: String): Intent {
    return Intent(Intent.ACTION_INSERT).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      setDataAndType(
        TASK_URI.buildUpon().appendEncodedPath(projectId).build(),
        TASK_CONTENT_ITEM_TYPE,
      )
    }
  }
}
