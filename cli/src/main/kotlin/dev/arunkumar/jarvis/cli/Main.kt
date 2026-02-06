package dev.arunkumar.jarvis.cli

import com.github.ajalt.clikt.core.main
import dev.arunkumar.jarvis.cli.commands.RootCommand

fun main(args: Array<String>) = RootCommand().main(args)
