package dev.arunkumar.jarvis.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import dev.arunkumar.jarvis.cli.client.JarvisClient
import dev.arunkumar.jarvis.shared.models.ai.AiProvider
import dev.arunkumar.jarvis.shared.models.ai.AiRequest
import kotlinx.coroutines.runBlocking

class AiCommand : CliktCommand(name = "ai") {
    private val serverUrl by requireObject<String>()
    private val prompt by argument("prompt")
        .help("Your prompt for the AI")

    override fun help(context: Context) = "AI chat interface"

    override fun run() = runBlocking {
        val client = JarvisClient(serverUrl)

        val request = AiRequest(
            provider = AiProvider.VERTEX_GEMINI,
            prompt = prompt
        )

        echo("Sending to AI: $prompt")
        val response = client.sendAiRequest(request)
        echo("AI Response: ${response.content}")
    }
}
