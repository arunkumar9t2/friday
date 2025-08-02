package dev.arunkumar.jarvis.data.permissions

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages pre-registered ActivityResultLaunchers for permission requests.
 * This must be initialized early in the activity lifecycle to avoid registration crashes.
 */
@Singleton
class PermissionLauncherManager @Inject constructor(
  private val permissionManager: PermissionManager
) {
  private var multiplePermissionsLauncher: ActivityResultLauncher<Array<String>>? = null
  private var currentCallback: ((Map<PermissionType, Boolean>) -> Unit)? = null
  private var currentPermissions: List<PermissionType> = emptyList()

  /**
   * Initialize the permission launcher - must be called before activity reaches STARTED state
   */
  fun initialize(activity: ComponentActivity) {
    multiplePermissionsLauncher = activity.registerForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
      handlePermissionResults(activity, results)
    }
  }

  /**
   * Request multiple runtime permissions using the pre-registered launcher
   */
  fun requestRuntimePermissions(
    activity: ComponentActivity,
    permissions: List<PermissionType>,
    onResult: (Map<PermissionType, Boolean>) -> Unit
  ) {
    val launcher = multiplePermissionsLauncher
    if (launcher == null) {
      // Fallback: launcher not initialized, return all denied
      onResult(permissions.associateWith { false })
      return
    }

    val permissionStrings = permissions
      .filter { it.protectionLevel == ProtectionLevel.DANGEROUS }
      .map { it.permission }
      .toTypedArray()

    if (permissionStrings.isEmpty()) {
      onResult(emptyMap())
      return
    }

    // Store callback and permissions for result handling
    currentCallback = onResult
    currentPermissions = permissions

    // Launch the permission request
    launcher.launch(permissionStrings)
  }

  /**
   * Request a single runtime permission
   */
  fun requestRuntimePermission(
    activity: ComponentActivity,
    permission: PermissionType,
    onResult: (Boolean) -> Unit
  ) {
    requestRuntimePermissions(activity, listOf(permission)) { results ->
      onResult(results[permission] ?: false)
    }
  }

  /**
   * Handle permission request results
   */
  private fun handlePermissionResults(
    activity: ComponentActivity,
    results: Map<String, Boolean>
  ) {
    val callback = currentCallback
    val permissions = currentPermissions

    if (callback == null || permissions.isEmpty()) {
      return
    }

    val permissionResults = mutableMapOf<PermissionType, Boolean>()

    permissions.forEach { permission ->
      val isGranted = results[permission.permission] ?: false
      permissionResults[permission] = isGranted

      // Update permission state in the manager
      val status = if (isGranted) {
        PermissionStatus.GRANTED
      } else {
        val canShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
          activity, permission.permission
        )
        if (canShowRationale) {
          PermissionStatus.DENIED
        } else {
          PermissionStatus.PERMANENTLY_DENIED
        }
      }

      permissionManager.updatePermissionState(
        permission = permission,
        status = status,
        canShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
          activity, permission.permission
        )
      )
    }

    // Clear the current state
    currentCallback = null
    currentPermissions = emptyList()

    // Invoke the callback
    callback(permissionResults)
  }

  /**
   * Check if the launcher manager is properly initialized
   */
  fun isInitialized(): Boolean = multiplePermissionsLauncher != null
}