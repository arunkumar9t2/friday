package dev.arunkumar.jarvis.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dev.arunkumar.jarvis.data.ticktick.TickTickSyncWorker
import dev.arunkumar.jarvis.R
import dev.arunkumar.jarvis.data.ticktick.TickTickIntents
import dev.arunkumar.jarvis.data.ticktick.TickTickNotificationItem
import dev.arunkumar.jarvis.data.ticktick.TickTickNotificationItem.TickTickTaskItem
import dev.arunkumar.jarvis.data.ticktick.TickTickRepository
import dev.arunkumar.jarvis.data.ticktick.relativeFormattedDate
import dev.arunkumar.jarvis.data.ticktick.toRemoteView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TickTickService : Service() {

  override fun onBind(intent: Intent) = null

  @Inject
  lateinit var repository: TickTickRepository

  @Inject
  lateinit var notificationManager: NotificationManager

  @Inject
  lateinit var workManager: WorkManager

  private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

  override fun onCreate() {
    super.onCreate()
    val notification = buildNotification(
      title = getString(R.string.ticktick_loading),
      tasks = emptyList(),
    )
    startForeground(NOTIFICATION_ID, notification)
  }

  override fun onDestroy() {
    serviceScope.cancel()
    super.onDestroy()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    intent?.let(::processIntent)
    return START_STICKY
  }

  private fun processIntent(incomingIntent: Intent) {
    when (incomingIntent.action) {
      ACTION_ADD_TASK -> {
        val replyContent = RemoteInput
          .getResultsFromIntent(incomingIntent)
          ?.getCharSequence(REPLY_ADD_TASK)
        if (replyContent != null) {
          val insertIntent = repository.getInsertTaskIntent(projectId = "0").apply {
            putExtra("title", replyContent.toString())
          }
          startActivity(insertIntent)
        }
        refreshNotification()
      }
      ACTION_REFRESH_NOTIFICATION -> refreshNotification()
    }
  }

  private fun refreshNotification() {
    serviceScope.launch {
      try {
        // Trigger one-time sync in background
        TickTickSyncWorker.enqueueOneTimeSync(workManager)
        val items = repository.getNotificationItems()
        val title = items.filterIsInstance<TickTickTaskItem>()
          .firstOrNull()
          ?.let { task ->
            "${task.dueDate.relativeFormattedDate()} - ${task.title}"
          } ?: getString(R.string.ticktick_tasks)
        notificationManager.notify(NOTIFICATION_ID, buildNotification(title, items))
      } catch (e: Exception) {
        Log.e(TAG, "Failed to refresh notification", e)
        notificationManager.notify(
          NOTIFICATION_ID,
          buildNotification(getString(R.string.ticktick_tasks), emptyList()),
        )
      }
    }
  }

  private fun buildNotification(
    title: CharSequence,
    tasks: List<TickTickNotificationItem>,
  ): Notification {
    ensureNotificationChannel()
    val remoteView = buildTaskView(tasks)

    val builder = NotificationCompat.Builder(this, TICKTICK_NOTIFICATION_CHANNEL)
      .setContentTitle(title)
      .setContentIntent(
        PendingIntent.getActivity(
          this,
          NOTIFICATION_ID,
          packageManager.getLaunchIntentForPackage(TickTickIntents.TICKTICK_PACKAGE)
            ?: Intent(),
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
      )
      .setCustomBigContentView(remoteView)
      .setStyle(NotificationCompat.DecoratedCustomViewStyle())
      .setColorized(true)
      .setColor(ContextCompat.getColor(this, R.color.ticktick_notification_color))
      .setSmallIcon(R.drawable.ic_jarvis_notification)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setShowWhen(false)
      .setCategory(Notification.CATEGORY_EVENT)
      .setOngoing(true)
      .setDeleteIntent(pendingTickTickIntent(ACTION_REFRESH_NOTIFICATION))

    val actualTasks = tasks.filterIsInstance<TickTickTaskItem>()
    if (actualTasks.isNotEmpty()) {
      builder.setCustomContentView(buildTaskView(listOf(actualTasks.first())))
      builder.setSubText(actualTasks.first().dueDate.relativeFormattedDate())
    }

    // Add task action with RemoteInput
    builder.addAction(
      NotificationCompat.Action.Builder(
        0,
        getString(R.string.ticktick_add_task),
        pendingTickTickIntent(ACTION_ADD_TASK, mutable = true),
      ).addRemoteInput(
        RemoteInput.Builder(REPLY_ADD_TASK)
          .setLabel(getString(R.string.ticktick_add_task))
          .build()
      ).setAuthenticationRequired(false)
        .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)
        .build()
    )

    // Refresh action
    builder.addAction(
      NotificationCompat.Action.Builder(
        0,
        getString(R.string.ticktick_refresh),
        pendingTickTickIntent(ACTION_REFRESH_NOTIFICATION),
      ).build()
    )

    return builder.build()
  }

  private fun buildTaskView(tasks: List<TickTickNotificationItem>): RemoteViews {
    return RemoteViews(packageName, R.layout.layout_ticktick_notification).apply {
      removeAllViews(R.id.ticktick_notification_tasks)
      tasks.map { it.toRemoteView(this@TickTickService) }
        .forEach { taskView ->
          addView(R.id.ticktick_notification_tasks, taskView)
        }
    }
  }

  private fun ensureNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        TICKTICK_NOTIFICATION_CHANNEL,
        getString(R.string.ticktick_channel_name),
        NotificationManager.IMPORTANCE_MIN,
      ).apply {
        description = getString(R.string.ticktick_channel_description)
      }
      notificationManager.createNotificationChannel(channel)
    }
  }

  companion object {
    private const val TAG = "TickTickService"
    private const val REPLY_ADD_TASK = "REPLY_ADD_TASK"
    private const val ACTION_ADD_TASK = "ACTION_ADD_TASK"
    private const val ACTION_REFRESH_NOTIFICATION = "ACTION_REFRESH_NOTIFICATION"
    private const val TICKTICK_NOTIFICATION_CHANNEL = "ticktick"
    private val NOTIFICATION_ID = "TickTick Service".hashCode()

    private fun Context.tickTickServiceIntent(action: String): Intent {
      return Intent(this, TickTickService::class.java).apply {
        this.action = action
      }
    }

    fun Context.startTickTickService(action: String = ACTION_REFRESH_NOTIFICATION) {
      ContextCompat.startForegroundService(this, tickTickServiceIntent(action))
    }

    fun Context.pendingTickTickIntent(
      action: String,
      mutable: Boolean = false,
    ): PendingIntent = PendingIntent.getService(
      this,
      action.hashCode(),
      tickTickServiceIntent(action),
      PendingIntent.FLAG_UPDATE_CURRENT or
        if (mutable) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_IMMUTABLE,
    )
  }
}
