package dev.arunkumar.jarvis.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/** Foreground service for assistant functionality (microphone and camera). */
class AssistantService : Service() {

  override fun onBind(intent: Intent?): IBinder? = null
}
