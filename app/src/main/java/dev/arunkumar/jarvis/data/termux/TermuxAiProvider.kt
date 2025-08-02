package dev.arunkumar.jarvis.data.termux

import android.util.Log
import dev.arunkumar.jarvis.data.ai.AiExecutionState
import dev.arunkumar.jarvis.data.ai.AiProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Termux implementation of AiProvider.
 * Executes AI scripts using Termux commands and maps results to AiExecutionState.
 */
@Singleton
class TermuxAiProvider @Inject constructor(
    private val commandExecutor: TermuxCommandExecutor
) : AiProvider {
    
    companion object {
        private const val TAG = "TermuxAiProvider"
        private const val EXECUTION_TIMEOUT_MS = 30_000L // 30 seconds
        private const val DEFAULT_SCRIPT_INTERPRETER = "bash"
    }
    
    override fun executeScript(
        script: String,
        parameters: Map<String, String>
    ): Flow<AiExecutionState> = flow {
        
        emit(AiExecutionState.Preparing)
        
        try {
            // Determine how to execute the script
            val commandRequest = prepareScriptCommand(script, parameters)
            
            Log.d(TAG, "Executing script with Termux: ${commandRequest.label}")
            
            emit(AiExecutionState.Running())
            
            // Execute with timeout
            val result = withTimeoutOrNull(EXECUTION_TIMEOUT_MS) {
                var lastResult: TermuxCommandResult? = null
                commandExecutor.executeCommand(commandRequest).collect { result ->
                    lastResult = result
                    // Emit intermediate progress if needed
                    if (result.hasOutput && !result.isSuccess) {
                        emit(AiExecutionState.Running(result.stdout))
                    }
                }
                lastResult
            }
            
            when {
                result == null -> {
                    emit(AiExecutionState.Error("Execution timeout", "Command timed out after ${EXECUTION_TIMEOUT_MS}ms"))
                }
                result.isSuccess -> {
                    val output = formatSuccessOutput(result)
                    emit(AiExecutionState.Success(output))
                }
                else -> {
                    val errorMsg = "Command failed (exit code: ${result.exitCode})"
                    val errorDetails = formatErrorDetails(result)
                    emit(AiExecutionState.Error(errorMsg, errorDetails))
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error executing script", e)
            emit(AiExecutionState.Error("Execution failed", "Unexpected error: ${e.message}"))
        }
        
    }.onStart {
        Log.d(TAG, "Starting AI script execution")
    }.catch { e ->
        Log.e(TAG, "Flow error during script execution", e)
        emit(AiExecutionState.Error("Flow error", "Stream error: ${e.message}"))
    }
    
    /**
     * Prepare a command request based on the script and parameters.
     */
    private fun prepareScriptCommand(
        script: String,
        parameters: Map<String, String>
    ): TermuxCommandRequest {
        
        // Check if script is a direct command path or needs interpretation
        val (commandPath, arguments) = if (script.startsWith("/") || script.contains("usr/bin")) {
            // Direct command path
            parseDirectCommand(script, parameters)
        } else {
            // Script content that needs to be interpreted
            prepareInterpretedScript(script, parameters)
        }
        
        return TermuxCommandRequest(
            commandPath = commandPath,
            arguments = arguments,
            workingDirectory = TermuxConstants.TERMUX_HOME_PATH,
            runInBackground = true,
            label = "AI Script Execution",
            description = "Executing AI script via Termux: ${script.take(50)}${if (script.length > 50) "..." else ""}"
        )
    }
    
    /**
     * Parse a direct command with parameters.
     */
    private fun parseDirectCommand(
        script: String,
        parameters: Map<String, String>
    ): Pair<String, List<String>> {
        val parts = script.trim().split("\\s+".toRegex())
        val commandPath = if (parts[0].startsWith("/")) {
            parts[0]
        } else {
            "${TermuxConstants.TERMUX_USR_BIN_PATH}/${parts[0]}"
        }
        
        val arguments = mutableListOf<String>()
        arguments.addAll(parts.drop(1))
        
        // Add parameters as arguments
        parameters.forEach { (key, value) ->
            arguments.add("--$key")
            arguments.add(value)
        }
        
        return commandPath to arguments
    }
    
    /**
     * Prepare script content for interpretation.
     * For direct script content, creates a command that echoes the script information.
     */
    private fun prepareInterpretedScript(
        script: String,
        parameters: Map<String, String>
    ): Pair<String, List<String>> {
        // Use echo to display script information
        // Future enhancement: write script to file and execute with bash
        val echoPath = "${TermuxConstants.TERMUX_USR_BIN_PATH}/echo"
        val message = "AI Script: $script (Parameters: $parameters)"
        
        return echoPath to listOf(message)
    }
    
    /**
     * Format successful command output.
     */
    private fun formatSuccessOutput(result: TermuxCommandResult): CharSequence {
        return if (result.hasOutput) {
            result.stdout.trim()
        } else {
            "Command completed successfully"
        }
    }
    
    /**
     * Format error details from command result.
     */
    private fun formatErrorDetails(result: TermuxCommandResult): CharSequence {
        val details = StringBuilder()
        
        if (result.stderr.isNotEmpty()) {
            details.append("STDERR:\n${result.stderr.trim()}\n\n")
        }
        
        if (result.stdout.isNotEmpty()) {
            details.append("STDOUT:\n${result.stdout.trim()}\n\n")
        }
        
        if (result.errorMessage.isNotEmpty()) {
            details.append("ERROR:\n${result.errorMessage.trim()}\n\n")
        }
        
        details.append("Exit Code: ${result.exitCode}\n")
        details.append("Error Code: ${result.errorCode}")
        
        return details.toString()
    }
}