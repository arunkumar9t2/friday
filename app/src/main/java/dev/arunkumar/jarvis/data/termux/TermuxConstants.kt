package dev.arunkumar.jarvis.data.termux

/**
 * Internal constants for Termux RUN_COMMAND integration.
 * Based on Termux RUN_COMMAND Intent documentation.
 */
internal object TermuxConstants {
    
    // Package and service information
    const val TERMUX_PACKAGE_NAME = "com.termux"
    const val TERMUX_RUN_COMMAND_SERVICE_NAME = "com.termux.app.RunCommandService"
    
    // Intent action
    const val ACTION_RUN_COMMAND = "com.termux.RUN_COMMAND"
    
    // Command extras
    const val EXTRA_COMMAND_PATH = "com.termux.RUN_COMMAND_PATH"
    const val EXTRA_ARGUMENTS = "com.termux.RUN_COMMAND_ARGUMENTS"
    const val EXTRA_WORKDIR = "com.termux.RUN_COMMAND_WORKDIR"
    const val EXTRA_BACKGROUND = "com.termux.RUN_COMMAND_BACKGROUND"
    const val EXTRA_SESSION_ACTION = "com.termux.RUN_COMMAND_SESSION_ACTION"
    const val EXTRA_COMMAND_LABEL = "com.termux.RUN_COMMAND_LABEL"
    const val EXTRA_COMMAND_DESCRIPTION = "com.termux.RUN_COMMAND_DESCRIPTION"
    const val EXTRA_PENDING_INTENT = "com.termux.RUN_COMMAND_PENDING_INTENT"
    
    // Result extras
    const val EXTRA_PLUGIN_RESULT_BUNDLE = "com.termux.RUN_COMMAND_RESULT_BUNDLE"
    const val EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT = "stdout"
    const val EXTRA_PLUGIN_RESULT_BUNDLE_STDERR = "stderr"
    const val EXTRA_PLUGIN_RESULT_BUNDLE_EXIT_CODE = "exitCode"
    const val EXTRA_PLUGIN_RESULT_BUNDLE_ERR = "errCode"
    const val EXTRA_PLUGIN_RESULT_BUNDLE_ERRMSG = "errmsg"
    const val EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT_ORIGINAL_LENGTH = "stdout_original_length"
    const val EXTRA_PLUGIN_RESULT_BUNDLE_STDERR_ORIGINAL_LENGTH = "stderr_original_length"
    
    // Common paths
    const val TERMUX_HOME_PATH = "/data/data/com.termux/files/home"
    const val TERMUX_USR_BIN_PATH = "/data/data/com.termux/files/usr/bin"
}