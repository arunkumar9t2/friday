package dev.arunkumar.jarvis.data.ai

import kotlinx.coroutines.flow.Flow

/**
 * Core abstraction for AI backend services.
 * Implementation agnostic - could be Termux, remote service, etc.
 */
interface AiProvider {
    /**
     * Execute a script/command with optional parameters.
     * 
     * @param script The script content or command to execute
     * @param parameters Optional key-value parameters for the script
     * @return Flow of execution states from preparation to completion
     */
    fun executeScript(
        script: String,
        parameters: Map<String, String> = emptyMap()
    ): Flow<AiExecutionState>
}