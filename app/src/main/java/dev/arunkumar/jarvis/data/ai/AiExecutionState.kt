package dev.arunkumar.jarvis.data.ai

/**
 * Represents the various states of AI script execution.
 * Uses CharSequence to support formatted text output.
 */
sealed class AiExecutionState {
    /**
     * Initial state - no execution in progress
     */
    object Idle : AiExecutionState()
    
    /**
     * Preparing to execute - setting up environment, validating script, etc.
     */
    object Preparing : AiExecutionState()
    
    /**
     * Currently executing the script
     * @param progress Optional progress information or intermediate output
     */
    data class Running(val progress: CharSequence? = null) : AiExecutionState()
    
    /**
     * Execution completed successfully
     * @param output The final output, potentially formatted
     */
    data class Success(val output: CharSequence) : AiExecutionState()
    
    /**
     * Execution failed
     * @param message Brief error description
     * @param details Optional detailed error information
     */
    data class Error(
        val message: String, 
        val details: CharSequence? = null
    ) : AiExecutionState()
}