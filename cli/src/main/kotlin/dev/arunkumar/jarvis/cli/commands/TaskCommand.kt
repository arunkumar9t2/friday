package dev.arunkumar.jarvis.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.arguments.optional
import dev.arunkumar.jarvis.cli.client.JarvisClient
import kotlinx.coroutines.runBlocking

class TaskCommand : CliktCommand(name = "tasks") {
    override fun help(context: Context) = "Manage tasks"

    init {
        subcommands(ListTasksCommand(), ShowTaskCommand())
    }

    override fun run() {}
}

class ListTasksCommand : CliktCommand(name = "list") {
    private val serverUrl by requireObject<String>()

    override fun help(context: Context) = "List all tasks"

    override fun run() = runBlocking {
        val client = JarvisClient(serverUrl)
        echo("TODO: Fetch and display tasks from $serverUrl")
    }
}

class ShowTaskCommand : CliktCommand(name = "show") {
    private val serverUrl by requireObject<String>()
    private val taskId by argument("task-id")
        .help("Task ID to show")
        .optional()

    override fun help(context: Context) = "Show task details"

    override fun run() = runBlocking {
        val client = JarvisClient(serverUrl)
        echo("TODO: Show task $taskId from $serverUrl")
    }
}
