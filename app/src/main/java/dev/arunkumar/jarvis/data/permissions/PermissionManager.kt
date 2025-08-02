package dev.arunkumar.jarvis.data.permissions

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Central manager for all permission-related operations */
@Singleton
class PermissionManager @Inject constructor(
  @ApplicationContext private val context: Context
) {

  private val _permissionState = MutableStateFlow(context.getCurrentAppPermissionState())
  val permissionState: StateFlow<AppPermissionState> = _permissionState.asStateFlow()

  /** Refresh the current permission state by checking all permissions */
  fun refreshPermissionState() {
    _permissionState.value = context.getCurrentAppPermissionState()
  }

  /** Get the current state of a specific permission */
  fun getPermissionState(permission: PermissionType): PermissionState? {
    return _permissionState.value.getPermissionState(permission)
  }

  /** Get the current state of a feature group */
  fun getFeatureGroupState(group: FeatureGroup): FeatureGroupState? {
    return _permissionState.value.getFeatureGroup(group)
  }

  /** Check if a specific permission is granted */
  fun isPermissionGranted(permission: PermissionType): Boolean {
    return context.isPermissionGranted(permission)
  }

  /** Check if all permissions in a feature group are granted */
  fun isFeatureGroupGranted(group: FeatureGroup): Boolean {
    return getFeatureGroupState(group)?.allGranted ?: false
  }

  /** Get all permissions that need user action (not granted) */
  fun getPermissionsNeedingAction(): List<PermissionState> {
    return _permissionState.value.allPermissions.filter { it.needsUserAction }
  }

  /** Get all dangerous permissions that can be requested at runtime */
  fun getRuntimePermissionsNeedingAction(): List<PermissionState> {
    return getPermissionsNeedingAction().filter {
      it.permission.protectionLevel == ProtectionLevel.DANGEROUS
    }
  }

  /** Get all special permissions that need settings navigation */
  fun getSpecialPermissionsNeedingAction(): List<PermissionState> {
    return getPermissionsNeedingAction().filter {
      it.permission.protectionLevel == ProtectionLevel.SPECIAL
    }
  }

  /** Update the state of a specific permission after a grant/deny event */
  fun updatePermissionState(
    permission: PermissionType,
    status: PermissionStatus,
    canShowRationale: Boolean = false
  ) {
    val currentState = _permissionState.value
    val updatedGroups = currentState.featureGroups.map { group ->
      val updatedPermissions = group.permissions.map { permState ->
        if (permState.permission == permission) {
          permState.copy(
            status = status,
            canShowRationale = canShowRationale,
            lastRequestTime = System.currentTimeMillis(),
            requestCount = permState.requestCount + 1
          )
        } else {
          permState
        }
      }
      group.copy(permissions = updatedPermissions)
    }

    _permissionState.value = currentState.copy(
      featureGroups = updatedGroups,
      lastUpdateTime = System.currentTimeMillis()
    )
  }

  /** Mark a permission as permanently denied */
  fun markPermissionPermanentlyDenied(permission: PermissionType) {
    updatePermissionState(permission, PermissionStatus.PERMANENTLY_DENIED)
  }

  /** Get the overall setup completion percentage */
  fun getSetupCompletionPercentage(): Float {
    return _permissionState.value.overallCompletionPercentage
  }

  /** Check if the app is fully set up (all required permissions granted) */
  fun isAppFullySetup(): Boolean {
    return _permissionState.value.isFullySetup
  }

  /** Get permissions by priority (most important first) */
  fun getPermissionsByPriority(): List<PermissionState> {
    return _permissionState.value.allPermissions
      .sortedWith(compareBy<PermissionState> { it.permission.featureGroup.priority }
        .thenBy { it.permission.protectionLevel }
      )
  }

  /** Get feature groups ordered by priority */
  fun getFeatureGroupsByPriority(): List<FeatureGroupState> {
    return _permissionState.value.featureGroups
      .sortedBy { it.group.priority }
  }
}
