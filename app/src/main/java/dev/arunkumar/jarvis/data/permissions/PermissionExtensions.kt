package dev.arunkumar.jarvis.data.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/** Extension functions for permission checking and management */

/** Check if a specific permission is granted */
fun Context.isPermissionGranted(permission: PermissionType): Boolean {
  return when (permission.protectionLevel) {
    ProtectionLevel.NORMAL -> true // Normal permissions are auto-granted
    ProtectionLevel.DANGEROUS -> {
      if (!permission.isRequiredForCurrentSdk) return true
      ContextCompat.checkSelfPermission(
        this,
        permission.permission
      ) == PackageManager.PERMISSION_GRANTED
    }

    ProtectionLevel.SPECIAL -> {
      when (permission) {
        PermissionType.SYSTEM_ALERT_WINDOW -> checkSystemAlertWindowPermission()
        PermissionType.WRITE_SETTINGS -> checkWriteSettingsPermission()
        PermissionType.NOTIFICATION_LISTENER -> checkNotificationListenerPermission()
        PermissionType.ACCESSIBILITY_SERVICE -> checkAccessibilityServicePermission()
        PermissionType.QUERY_ALL_PACKAGES -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContextCompat.checkSelfPermission(
              this,
              permission.permission
            ) == PackageManager.PERMISSION_GRANTED
          } else true
        }

        else -> false
      }
    }

    ProtectionLevel.SIGNATURE -> false // We can't get signature permissions as a regular app
  }
}

/** Check system alert window permission */
private fun Context.checkSystemAlertWindowPermission(): Boolean {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    android.provider.Settings.canDrawOverlays(this)
  } else {
    true // Auto-granted on older versions
  }
}

/** Check write settings permission */
private fun Context.checkWriteSettingsPermission(): Boolean {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    android.provider.Settings.System.canWrite(this)
  } else {
    true // Auto-granted on older versions
  }
}

/** Check notification listener permission */
private fun Context.checkNotificationListenerPermission(): Boolean {
  val enabledNotificationListeners = android.provider.Settings.Secure.getString(
    contentResolver,
    "enabled_notification_listeners"
  )
  return enabledNotificationListeners?.contains(packageName) == true
}

/** Check accessibility service permission */
private fun Context.checkAccessibilityServicePermission(): Boolean {
  val enabledServices = android.provider.Settings.Secure.getString(
    contentResolver,
    android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
  )
  return enabledServices?.contains(packageName) == true
}

/** Get all permissions that require runtime requests for current SDK */
fun getAllRuntimePermissions(): List<PermissionType> {
  return PermissionType.values().filter { permission ->
    permission.protectionLevel == ProtectionLevel.DANGEROUS &&
      permission.isRequiredForCurrentSdk
  }
}

/** Get all special permissions that require settings navigation */
fun getAllSpecialPermissions(): List<PermissionType> {
  return PermissionType.values().filter { permission ->
    permission.protectionLevel == ProtectionLevel.SPECIAL &&
      permission.isRequiredForCurrentSdk
  }
}

/** Get permissions grouped by feature */
fun getPermissionsByFeatureGroup(): Map<FeatureGroup, List<PermissionType>> {
  return PermissionType.values()
    .filter { it.isRequiredForCurrentSdk }
    .groupBy { it.featureGroup }
    .toSortedMap(compareBy { it.priority })
}

/** Get all permissions for a specific feature group */
fun getPermissionsForFeatureGroup(group: FeatureGroup): List<PermissionType> {
  return PermissionType.values().filter {
    it.featureGroup == group && it.isRequiredForCurrentSdk
  }
}

/** Get current permission state for a specific permission */
fun Context.getPermissionState(permission: PermissionType): PermissionState {
  val isGranted = isPermissionGranted(permission)
  val status = when {
    !permission.isRequiredForCurrentSdk -> PermissionStatus.NOT_APPLICABLE
    isGranted -> PermissionStatus.GRANTED
    else -> PermissionStatus.NOT_REQUESTED // Will be updated by permission manager
  }

  return PermissionState(
    permission = permission,
    status = status,
    isRequired = permission.isRequiredForCurrentSdk
  )
}

/** Get current app permission state */
fun Context.getCurrentAppPermissionState(): AppPermissionState {
  val featureGroups = getPermissionsByFeatureGroup().map { (group, permissions) ->
    val permissionStates = permissions.map { getPermissionState(it) }
    FeatureGroupState(
      group = group,
      permissions = permissionStates,
      isRequired = true
    )
  }

  return AppPermissionState(featureGroups = featureGroups)
}
