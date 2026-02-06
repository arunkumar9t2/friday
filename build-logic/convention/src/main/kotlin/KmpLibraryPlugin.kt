import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply("org.jetbrains.kotlin.plugin.serialization")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            extensions.configure<KotlinMultiplatformExtension> {
                jvm()

                sourceSets.apply {
                    commonMain.dependencies {
                        implementation(libs.findLibrary("kotlinx-serialization-json").get())
                        implementation(libs.findLibrary("kotlinx-coroutines-core").get())
                        implementation(libs.findLibrary("kotlinx-datetime").get())
                    }
                    commonTest.dependencies {
                        implementation(kotlin("test"))
                    }
                }
            }
        }
    }
}
