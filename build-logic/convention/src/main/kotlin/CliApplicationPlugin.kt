import org.gradle.api.Plugin
import org.gradle.api.Project

class CliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply("org.jetbrains.kotlin.plugin.serialization")
            plugins.apply("application")
        }
    }
}
