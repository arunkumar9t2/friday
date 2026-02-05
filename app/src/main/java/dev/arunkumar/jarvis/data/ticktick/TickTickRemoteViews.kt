package dev.arunkumar.jarvis.data.ticktick

import android.app.PendingIntent
import android.content.Context
import android.text.format.DateUtils
import android.widget.RemoteViews
import dev.arunkumar.jarvis.R

fun TickTickNotificationItem.toRemoteView(context: Context): RemoteViews = when (this) {
  is TickTickNotificationItem.TickTickTaskItem -> buildRemoteView(context)
  is TickTickNotificationItem.TickTickOverdue -> {
    RemoteViews(context.packageName, R.layout.layout_ticktick_notification_separator)
  }
  is TickTickNotificationItem.TodaySeparator -> {
    RemoteViews(context.packageName, R.layout.layout_ticktick_notification_separator).apply {
      setTextViewText(R.id.ticktick_notification_separator, context.getString(R.string.ticktick_upcoming))
    }
  }
}

private fun TickTickNotificationItem.TickTickTaskItem.buildRemoteView(
  context: Context,
): RemoteViews {
  return RemoteViews(context.packageName, R.layout.layout_ticktick_notification_item).apply {
    setTextViewText(R.id.ticktick_notification_title, title)

    val taskDate = if (DateUtils.isToday(dueDate)) {
      context.getString(R.string.ticktick_today)
    } else {
      DateUtils.getRelativeDateTimeString(
        context,
        dueDate,
        DateUtils.DAY_IN_MILLIS,
        DateUtils.DAY_IN_MILLIS,
        0,
      ).toString().split(",").first()
    }
    setTextViewText(R.id.ticktick_notification_date, taskDate)

    val taskDueTime = DateUtils.getRelativeDateTimeString(
      context,
      dueDate,
      DateUtils.DAY_IN_MILLIS,
      DateUtils.DAY_IN_MILLIS,
      0,
    ).toString()
      .split(",")
      .let { chunks -> chunks.lastOrNull() ?: chunks.first() }
      .trim()
    setTextViewText(R.id.ticktick_notification_due, taskDueTime)

    val priorityColor = TickTickPriority.fromLevel(priority).color
    setInt(R.id.ticktick_notification_priority, "setBackgroundColor", priorityColor)

    setTextViewText(R.id.ticktick_notification_project_name, projectName)

    setOnClickPendingIntent(
      R.id.ticktick_notification_item_root,
      PendingIntent.getActivity(
        context,
        taskId.hashCode(),
        TickTickIntents.viewTaskIntent(projectId, taskId),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      ),
    )
  }
}
