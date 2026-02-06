import org.gradle.api.Plugin
import org.gradle.api.Project

class KtorServerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("io.ktor.plugin")
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply("org.jetbrains.kotlin.plugin.serialization")
        }
    }
}
