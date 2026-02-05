package dev.arunkumar.jarvis.data.ticktick

sealed class TickTickNotificationItem {

  data class TickTickTaskItem(
    val taskId: String,
    val projectId: String,
    val title: String,
    val dueDate: Long,
    val priority: Int,
    val projectName: String,
    val color: String,
  ) : TickTickNotificationItem()

  data object TickTickOverdue : TickTickNotificationItem()

  data object TodaySeparator : TickTickNotificationItem()
}
