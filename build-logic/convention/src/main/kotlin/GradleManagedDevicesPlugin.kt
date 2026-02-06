import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class GradleManagedDevicesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.withId("com.android.application") {
            target.extensions.configure<com.android.build.api.dsl.ApplicationExtension> {
                configureManagedDevices(this)
            }
        }
        target.plugins.withId("com.android.library") {
            target.extensions.configure<com.android.build.api.dsl.LibraryExtension> {
                configureManagedDevices(this)
            }
        }
    }

    private fun configureManagedDevices(android: CommonExtension<*, *, *, *, *, *>) {
        android.testOptions {
            animationsDisabled = true

            managedDevices {
                localDevices.maybeCreate("pixel6Api36").apply {
                    device = "Pixel 6"
                    apiLevel = 36
                    systemImageSource = "google_apis_playstore"
                    testedAbi = "x86_64"
                }

                groups.maybeCreate("phone").apply {
                    targetDevices.addAll(localDevices.matching { it.name == "pixel6Api36" })
                }
            }
        }
    }
}
