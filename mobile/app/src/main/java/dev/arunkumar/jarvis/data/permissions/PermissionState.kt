package dev.arunkumar.jarvis.data.permissions

import androidx.compose.runtime.Immutable

/** Represents the current state of a single permission */
@Immutable
data class PermissionState(
  val permission: PermissionType,
  val status: PermissionStatus,
  val isRequired: Boolean = true,
  val lastRequestTime: Long = 0L,
  val requestCount: Int = 0,
  val canShowRationale: Boolean = false
) {
  val isGranted: Boolean
    get() = status == PermissionStatus.GRANTED

  val isDenied: Boolean
    get() = status == PermissionStatus.DENIED

  val isPermanentlyDenied: Boolean
    get() = status == PermissionStatus.PERMANENTLY_DENIED

  val needsUserAction: Boolean
    get() = status in listOf(
      PermissionStatus.NOT_REQUESTED,
      PermissionStatus.DENIED,
      PermissionStatus.PERMANENTLY_DENIED
    )
}

/** Permission status enumeration */
enum class PermissionStatus {
  NOT_REQUESTED,       // Never asked for this permission
  GRANTED,            // Permission is granted
  DENIED,             // Permission denied but can ask again
  PERMANENTLY_DENIED,  // Permission denied with "Don't ask again"
  NOT_APPLICABLE      // Permission not applicable for current SDK
}

/** Represents the overall state of a feature group's permissions */
@Immutable
data class FeatureGroupState(
  val group: FeatureGroup,
  val permissions: List<PermissionState>,
  val isRequired: Boolean = true
) {
  val grantedCount: Int
    get() = permissions.count { it.isGranted }

  val totalCount: Int
    get() = permissions.size

  val allGranted: Boolean
    get() = permissions.all { it.isGranted }

  val noneGranted: Boolean
    get() = permissions.none { it.isGranted }

  val partiallyGranted: Boolean
    get() = grantedCount > 0 && !allGranted

  val completionPercentage: Float
    get() = if (totalCount > 0) grantedCount.toFloat() / totalCount else 0f

  val overallStatus: FeatureStatus
    get() = when {
      allGranted -> FeatureStatus.COMPLETE
      partiallyGranted -> FeatureStatus.PARTIAL
      else -> FeatureStatus.INCOMPLETE
    }
}

/** Feature completion status */
enum class FeatureStatus {
  COMPLETE,    // All permissions granted
  PARTIAL,     // Some permissions granted
  INCOMPLETE   // No permissions granted
}

/** Complete permission state for the entire app */
@Immutable
data class AppPermissionState(
  val featureGroups: List<FeatureGroupState>,
  val lastUpdateTime: Long = System.currentTimeMillis()
) {
  val allPermissions: List<PermissionState>
    get() = featureGroups.flatMap { it.permissions }

  val grantedPermissions: List<PermissionState>
    get() = allPermissions.filter { it.isGranted }

  val deniedPermissions: List<PermissionState>
    get() = allPermissions.filter { it.isDenied || it.isPermanentlyDenied }

  val overallCompletionPercentage: Float
    get() {
      val total = allPermissions.size
      val granted = grantedPermissions.size
      return if (total > 0) granted.toFloat() / total else 0f
    }

  val isFullySetup: Boolean
    get() = featureGroups.all { !it.isRequired || it.allGranted }

  fun getFeatureGroup(group: FeatureGroup): FeatureGroupState? {
    return featureGroups.find { it.group == group }
  }

  fun getPermissionState(permission: PermissionType): PermissionState? {
    return allPermissions.find { it.permission == permission }
  }
}
