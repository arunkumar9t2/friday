package dev.arunkumar.jarvis.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option

class RootCommand : CliktCommand(name = "jarvis") {
    private val serverUrl by option("--server-url")
        .help("Backend server URL")
        .default("http://localhost:8080")

    override fun help(context: Context) = "Jarvis CLI - Your AI-powered assistant"

    override fun run() {
        currentContext.findOrSetObject { serverUrl }
    }

    init {
        subcommands(TaskCommand(), AiCommand())
    }
}
