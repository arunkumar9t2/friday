package dev.arunkumar.jarvis.cli

import dev.arunkumar.jarvis.cli.commands.RootCommand
import kotlin.test.Test
import kotlin.test.assertTrue

class CommandTest {
    @Test
    fun testHelpOutput() {
        val command = RootCommand()
        val help = command.getFormattedHelp() ?: ""
        assertTrue(help.contains("Jarvis CLI"))
    }
}
