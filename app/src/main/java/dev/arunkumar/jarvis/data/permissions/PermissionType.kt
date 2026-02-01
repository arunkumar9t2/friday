package dev.arunkumar.jarvis.data.permissions

import android.Manifest
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** Defines all permissions required by the assistant app */
enum class PermissionType(
  val permission: String,
  val protectionLevel: ProtectionLevel,
  val minSdkVersion: Int = 1,
  val displayName: String,
  val description: String,
  val featureGroup: FeatureGroup
) {
  // ==================== DANGEROUS PERMISSIONS ====================
  RECORD_AUDIO(
    permission = Manifest.permission.RECORD_AUDIO,
    protectionLevel = ProtectionLevel.DANGEROUS,
    displayName = "Microphone",
    description = "Record audio for voice commands and conversations",
    featureGroup = FeatureGroup.VOICE_AUDIO
  ),

  CAMERA(
    permission = Manifest.permission.CAMERA,
    protectionLevel = ProtectionLevel.DANGEROUS,
    displayName = "Camera",
    description = "Access camera for visual assistance and scanning",
    featureGroup = FeatureGroup.CAMERA_MEDIA
  ),

  POST_NOTIFICATIONS(
    permission = Manifest.permission.POST_NOTIFICATIONS,
    protectionLevel = ProtectionLevel.DANGEROUS,
    minSdkVersion = Build.VERSION_CODES.TIRAMISU,
    displayName = "Post Notifications",
    description = "Send notifications and alerts",
    featureGroup = FeatureGroup.CORE_ASSISTANT
  ),

  READ_SMS(
    permission = Manifest.permission.READ_SMS,
    protectionLevel = ProtectionLevel.DANGEROUS,
    displayName = "Read SMS",
    description = "Read text messages for assistance",
    featureGroup = FeatureGroup.MESSAGES_CONTACTS
  ),

  SEND_SMS(
    permission = Manifest.permission.SEND_SMS,
    protectionLevel = ProtectionLevel.DANGEROUS,
    displayName = "Send SMS",
    description = "Send text messages on your behalf",
    featureGroup = FeatureGroup.MESSAGES_CONTACTS
  ),

  READ_CONTACTS(
    permission = Manifest.permission.READ_CONTACTS,
    protectionLevel = ProtectionLevel.DANGEROUS,
    displayName = "Read Contacts",
    description = "Access your contacts for smart assistance",
    featureGroup = FeatureGroup.MESSAGES_CONTACTS
  ),

  WRITE_CONTACTS(
    permission = Manifest.permission.WRITE_CONTACTS,
    protectionLevel = ProtectionLevel.DANGEROUS,
    displayName = "Write Contacts",
    description = "Create and modify contacts",
    featureGroup = FeatureGroup.MESSAGES_CONTACTS
  ),

  READ_PHONE_STATE(
    permission = Manifest.permission.READ_PHONE_STATE,
    protectionLevel = ProtectionLevel.DANGEROUS,
    displayName = "Phone State",
    description = "Access phone status and identity",
    featureGroup = FeatureGroup.MESSAGES_CONTACTS
  ),

  // ==================== SPECIAL PERMISSIONS ====================
  SYSTEM_ALERT_WINDOW(
    permission = Manifest.permission.SYSTEM_ALERT_WINDOW,
    protectionLevel = ProtectionLevel.SPECIAL,
    displayName = "Display Over Apps",
    description = "Show overlay UI for quick assistant access",
    featureGroup = FeatureGroup.CORE_ASSISTANT
  ),

  WRITE_SETTINGS(
    permission = Manifest.permission.WRITE_SETTINGS,
    protectionLevel = ProtectionLevel.SPECIAL,
    displayName = "Modify System Settings",
    description = "Adjust system settings for automation",
    featureGroup = FeatureGroup.SYSTEM_INTEGRATION
  ),

  NOTIFICATION_LISTENER(
    permission = "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE",
    protectionLevel = ProtectionLevel.SPECIAL,
    displayName = "Notification Access",
    description = "Read and interact with all notifications",
    featureGroup = FeatureGroup.CORE_ASSISTANT
  ),

  ACCESSIBILITY_SERVICE(
    permission = "android.permission.BIND_ACCESSIBILITY_SERVICE",
    protectionLevel = ProtectionLevel.SPECIAL,
    displayName = "Accessibility Service",
    description = "System-wide assistance and automation",
    featureGroup = FeatureGroup.CORE_ASSISTANT
  ),

  QUERY_ALL_PACKAGES(
    permission = Manifest.permission.QUERY_ALL_PACKAGES,
    protectionLevel = ProtectionLevel.SPECIAL,
    minSdkVersion = Build.VERSION_CODES.R,
    displayName = "Query All Packages",
    description = "See all installed apps for smart integration",
    featureGroup = FeatureGroup.SYSTEM_INTEGRATION
  ),

  // ==================== THIRD-PARTY PERMISSIONS ====================
  READ_TICKTICK_TASKS(
    permission = "com.ticktick.task.permission.READ_TASKS",
    protectionLevel = ProtectionLevel.NORMAL,
    displayName = "TickTick Tasks",
    description = "Read tasks from TickTick for display and management",
    featureGroup = FeatureGroup.APP_INTEGRATION
  );

  val isRequiredForCurrentSdk: Boolean
    get() = Build.VERSION.SDK_INT >= minSdkVersion

  /** Check if this permission should be shown in UI (not auto-granted) */
  val shouldShowInUI: Boolean
    get() = when {
      !isRequiredForCurrentSdk -> false
      this == QUERY_ALL_PACKAGES && Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> false
      else -> true
    }
}

/** Permission protection levels determine how they're granted */
enum class ProtectionLevel {
  NORMAL,      // Auto-granted
  DANGEROUS,   // Runtime request required
  SPECIAL,     // Settings redirect required
  SIGNATURE    // System signature required
}

/** Feature-based grouping of permissions for UI organization */
enum class FeatureGroup(
  val displayName: String,
  val description: String,
  val icon: ImageVector,
  val priority: Int
) {
  CORE_ASSISTANT(
    displayName = "Core Assistant",
    description = "Essential permissions for basic assistant functionality",
    icon = Icons.Filled.AccountCircle,
    priority = 1
  ),

  VOICE_AUDIO(
    displayName = "Voice & Audio",
    description = "Voice commands and audio interaction",
    icon = Icons.Filled.Call,
    priority = 2
  ),

  CAMERA_MEDIA(
    displayName = "Camera & Media",
    description = "Visual assistance and media access",
    icon = Icons.Filled.Info,
    priority = 3
  ),

  MESSAGES_CONTACTS(
    displayName = "Messages & Contacts",
    description = "SMS and contact management",
    icon = Icons.Filled.Email,
    priority = 4
  ),

  SYSTEM_INTEGRATION(
    displayName = "System Integration",
    description = "Deep system access and automation",
    icon = Icons.Filled.Settings,
    priority = 5
  ),

  APP_INTEGRATION(
    displayName = "App Integration",
    description = "Third-party app connections",
    icon = Icons.Filled.Build,
    priority = 6
  )
}
