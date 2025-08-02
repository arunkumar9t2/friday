package dev.arunkumar.jarvis.data.termux

/**
 * Represents the result of a Termux command execution.
 */
data class TermuxCommandResult(
    val executionId: Int,
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val errorCode: Int,
    val errorMessage: String,
    val stdoutOriginalLength: String,
    val stderrOriginalLength: String
) {
    val isSuccess: Boolean
        get() = exitCode == 0 && errorCode == 0
        
    val hasOutput: Boolean
        get() = stdout.isNotEmpty()
        
    val hasError: Boolean
        get() = stderr.isNotEmpty() || errorMessage.isNotEmpty()
}