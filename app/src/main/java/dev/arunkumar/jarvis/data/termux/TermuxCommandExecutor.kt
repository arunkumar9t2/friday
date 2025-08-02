package dev.arunkumar.jarvis.data.termux

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Low-level executor for Termux RUN_COMMAND intents.
 * Handles intent building, execution, and result coordination.
 */
@Singleton
class TermuxCommandExecutor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val executionIdGenerator = AtomicInteger(1000)
    private val pendingExecutions = mutableMapOf<Int, Channel<TermuxCommandResult>>()
    
    companion object {
        private const val TAG = "TermuxCommandExecutor"
    }
    
    /**
     * Execute a command and return a flow of results.
     */
    fun executeCommand(request: TermuxCommandRequest): Flow<TermuxCommandResult> {
        val executionId = executionIdGenerator.getAndIncrement()
        val resultChannel = Channel<TermuxCommandResult>(1)
        
        pendingExecutions[executionId] = resultChannel
        
        try {
            val intent = buildCommandIntent(request, executionId)
            ContextCompat.startForegroundService(context, intent)
            Log.d(TAG, "Started Termux command with execution ID: $executionId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute command", e)
            // Send error result
            val errorResult = TermuxCommandResult(
                executionId = executionId,
                stdout = "",
                stderr = "",
                exitCode = -1,
                errorCode = -1,
                errorMessage = "Failed to start command: ${e.message}",
                stdoutOriginalLength = "0",
                stderrOriginalLength = "0"
            )
            resultChannel.trySend(errorResult)
            resultChannel.close()
            pendingExecutions.remove(executionId)
        }
        
        return resultChannel.receiveAsFlow()
    }
    
    /**
     * Handle result from TermuxResultReceiver.
     */
    fun handleCommandResult(result: TermuxCommandResult) {
        val channel = pendingExecutions.remove(result.executionId)
        if (channel != null) {
            channel.trySend(result)
            channel.close()
            Log.d(TAG, "Delivered result for execution ID: ${result.executionId}")
        } else {
            Log.w(TAG, "Received result for unknown execution ID: ${result.executionId}")
        }
    }
    
    /**
     * Build the RUN_COMMAND intent based on the request.
     */
    private fun buildCommandIntent(request: TermuxCommandRequest, executionId: Int): Intent {
        val intent = Intent().apply {
            setClassName(TermuxConstants.TERMUX_PACKAGE_NAME, TermuxConstants.TERMUX_RUN_COMMAND_SERVICE_NAME)
            action = TermuxConstants.ACTION_RUN_COMMAND
            putExtra(TermuxConstants.EXTRA_COMMAND_PATH, request.commandPath)
            putExtra(TermuxConstants.EXTRA_WORKDIR, request.workingDirectory)
            putExtra(TermuxConstants.EXTRA_BACKGROUND, request.runInBackground)
            putExtra(TermuxConstants.EXTRA_SESSION_ACTION, "0") // Default session action
            
            // Add arguments if any
            if (request.arguments.isNotEmpty()) {
                putExtra(TermuxConstants.EXTRA_ARGUMENTS, request.arguments.toTypedArray())
            }
            
            // Add optional label and description
            request.label?.let { putExtra(TermuxConstants.EXTRA_COMMAND_LABEL, it) }
            request.description?.let { putExtra(TermuxConstants.EXTRA_COMMAND_DESCRIPTION, it) }
            
            // Add pending intent for result
            val resultIntent = Intent(context, TermuxResultReceiver::class.java).apply {
                putExtra(TermuxResultReceiver.EXTRA_EXECUTION_ID, executionId)
            }
            
            val pendingIntent = PendingIntent.getService(
                context, 
                executionId, 
                resultIntent,
                PendingIntent.FLAG_ONE_SHOT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_MUTABLE
                } else {
                    0
                }
            )
            
            putExtra(TermuxConstants.EXTRA_PENDING_INTENT, pendingIntent)
        }
        
        return intent
    }
}