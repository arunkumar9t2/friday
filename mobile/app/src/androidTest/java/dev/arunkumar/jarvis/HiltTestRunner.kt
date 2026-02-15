package dev.arunkumar.jarvis

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {
  override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
    return super.newApplication(cl, HiltTestApplication::class.java.name, context)
  }

  override fun onStart() {
    // Initialize WorkManager before tests start
    val context = targetContext.applicationContext
    if (!WorkManager.isInitialized()) {
      WorkManager.initialize(
        context,
        Configuration.Builder()
          .setMinimumLoggingLevel(android.util.Log.DEBUG)
          .build()
      )
    }
    super.onStart()
  }
}
