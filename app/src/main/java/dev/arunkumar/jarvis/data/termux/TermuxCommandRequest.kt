package dev.arunkumar.jarvis.data.termux

/**
 * Represents a command request to be executed by Termux.
 */
data class TermuxCommandRequest(
    val commandPath: String,
    val arguments: List<String> = emptyList(),
    val workingDirectory: String = TermuxConstants.TERMUX_HOME_PATH,
    val runInBackground: Boolean = true,
    val label: String? = null,
    val description: String? = null
)