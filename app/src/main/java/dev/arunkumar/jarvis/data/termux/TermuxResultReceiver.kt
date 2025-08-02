package dev.arunkumar.jarvis.data.termux

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * IntentService that receives results from Termux command executions.
 * Processes the result bundle and forwards to the command executor.
 */
@AndroidEntryPoint
internal class TermuxResultReceiver : IntentService("TermuxResultReceiver") {
    
    @Inject
    lateinit var commandExecutor: TermuxCommandExecutor
    
    companion object {
        private const val TAG = "TermuxResultReceiver"
        const val EXTRA_EXECUTION_ID = "execution_id"
    }
    
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            Log.w(TAG, "Received null intent")
            return
        }
        
        Log.d(TAG, "Received execution result")
        
        val executionId = intent.getIntExtra(EXTRA_EXECUTION_ID, 0)
        if (executionId == 0) {
            Log.e(TAG, "Invalid execution ID received")
            return
        }
        
        val resultBundle = intent.getBundleExtra(TermuxConstants.EXTRA_PLUGIN_RESULT_BUNDLE)
        if (resultBundle == null) {
            Log.e(TAG, "No result bundle found in intent")
            handleError(executionId, "No result bundle received")
            return
        }
        
        try {
            val result = parseResultBundle(executionId, resultBundle)
            commandExecutor.handleCommandResult(result)
            
            Log.d(TAG, "Processed result for execution ID $executionId: " +
                    "exitCode=${result.exitCode}, " +
                    "hasOutput=${result.hasOutput}, " +
                    "hasError=${result.hasError}")
        } catch (e: Exception) {
            Log.e(TAG, "Error processing result for execution ID $executionId", e)
            handleError(executionId, "Error processing result: ${e.message}")
        }
    }
    
    /**
     * Parse the result bundle from Termux into our internal result format.
     */
    private fun parseResultBundle(executionId: Int, bundle: Bundle): TermuxCommandResult {
        return TermuxCommandResult(
            executionId = executionId,
            stdout = bundle.getString(TermuxConstants.EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT, ""),
            stderr = bundle.getString(TermuxConstants.EXTRA_PLUGIN_RESULT_BUNDLE_STDERR, ""),
            exitCode = bundle.getInt(TermuxConstants.EXTRA_PLUGIN_RESULT_BUNDLE_EXIT_CODE, -1),
            errorCode = bundle.getInt(TermuxConstants.EXTRA_PLUGIN_RESULT_BUNDLE_ERR, 0),
            errorMessage = bundle.getString(TermuxConstants.EXTRA_PLUGIN_RESULT_BUNDLE_ERRMSG, ""),
            stdoutOriginalLength = bundle.getString(TermuxConstants.EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT_ORIGINAL_LENGTH, "0"),
            stderrOriginalLength = bundle.getString(TermuxConstants.EXTRA_PLUGIN_RESULT_BUNDLE_STDERR_ORIGINAL_LENGTH, "0")
        )
    }
    
    /**
     * Handle errors by creating an error result.
     */
    private fun handleError(executionId: Int, errorMessage: String) {
        val errorResult = TermuxCommandResult(
            executionId = executionId,
            stdout = "",
            stderr = "",
            exitCode = -1,
            errorCode = -1,
            errorMessage = errorMessage,
            stdoutOriginalLength = "0",
            stderrOriginalLength = "0"
        )
        commandExecutor.handleCommandResult(errorResult)
    }
}