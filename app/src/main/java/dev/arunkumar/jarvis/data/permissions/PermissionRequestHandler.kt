package dev.arunkumar.jarvis.data.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.arunkumar.jarvis.R
import javax.inject.Inject
import javax.inject.Singleton

/** Handles permission requests for different types of permissions */
@Singleton
class PermissionRequestHandler @Inject constructor(
  @ApplicationContext private val context: Context,
  private val permissionManager: PermissionManager,
  private val permissionLauncherManager: PermissionLauncherManager
) {

  /** Request multiple runtime permissions */
  fun requestRuntimePermissions(
    activity: ComponentActivity,
    permissions: List<PermissionType>,
    onResult: (Map<PermissionType, Boolean>) -> Unit
  ) {
    permissionLauncherManager.requestRuntimePermissions(activity, permissions, onResult)
  }

  /** Request a single runtime permission */
  fun requestRuntimePermission(
    activity: ComponentActivity,
    permission: PermissionType,
    onResult: (Boolean) -> Unit
  ) {
    permissionLauncherManager.requestRuntimePermission(activity, permission, onResult)
  }

  /** Navigate to settings for special permissions */
  fun requestSpecialPermission(
    activity: Activity,
    permission: PermissionType,
    onResult: (() -> Unit)? = null
  ) {
    val intent = when (permission) {
      PermissionType.SYSTEM_ALERT_WINDOW -> createOverlaySettingsIntent()
      PermissionType.WRITE_SETTINGS -> createWriteSettingsIntent()
      PermissionType.NOTIFICATION_LISTENER -> createNotificationListenerIntent()
      PermissionType.ACCESSIBILITY_SERVICE -> createAccessibilitySettingsIntent()
      else -> null
    }

    intent?.let {
      try {
        activity.startActivity(it)
        onResult?.invoke()
      } catch (e: Exception) {
        // Fallback to general settings if specific intent fails
        activity.startActivity(Intent(Settings.ACTION_SETTINGS))
        onResult?.invoke()
      }
    }
  }

  /** Create intent for overlay permission settings */
  private fun createOverlaySettingsIntent(): Intent {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
      )
    } else {
      Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:${context.packageName}")
      }
    }
  }

  /** Create intent for write settings permission */
  private fun createWriteSettingsIntent(): Intent {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Intent(
        Settings.ACTION_MANAGE_WRITE_SETTINGS,
        Uri.parse("package:${context.packageName}")
      )
    } else {
      Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:${context.packageName}")
      }
    }
  }

  /** Create intent for notification listener settings */
  private fun createNotificationListenerIntent(): Intent {
    return Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
  }

  /** Create intent for accessibility settings */
  private fun createAccessibilitySettingsIntent(): Intent {
    return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
  }

  /** Check if we should show rationale for a permission */
  fun shouldShowRationale(activity: Activity, permission: PermissionType): Boolean {
    return if (permission.protectionLevel == ProtectionLevel.DANGEROUS) {
      ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.permission)
    } else {
      false
    }
  }

  /** Get a user-friendly explanation for why a permission is needed */
  fun getPermissionRationale(permission: PermissionType): String {
    return when (permission) {
      PermissionType.RECORD_AUDIO ->
        context.getString(R.string.permission_rationale_record_audio)

      PermissionType.CAMERA ->
        context.getString(R.string.permission_rationale_camera)

      PermissionType.POST_NOTIFICATIONS ->
        context.getString(R.string.permission_rationale_post_notifications)

      PermissionType.READ_SMS, PermissionType.SEND_SMS ->
        context.getString(R.string.permission_rationale_sms)

      PermissionType.READ_CONTACTS, PermissionType.WRITE_CONTACTS ->
        context.getString(R.string.permission_rationale_contacts)

      PermissionType.READ_PHONE_STATE ->
        context.getString(R.string.permission_rationale_read_phone_state)

      PermissionType.SYSTEM_ALERT_WINDOW ->
        context.getString(R.string.permission_rationale_system_alert_window)

      PermissionType.WRITE_SETTINGS ->
        context.getString(R.string.permission_rationale_write_settings)

      PermissionType.NOTIFICATION_LISTENER ->
        context.getString(R.string.permission_rationale_notification_listener)

      PermissionType.ACCESSIBILITY_SERVICE ->
        context.getString(R.string.permission_rationale_accessibility_service)

      PermissionType.QUERY_ALL_PACKAGES ->
        context.getString(R.string.permission_rationale_query_all_packages)
    }
  }
}
