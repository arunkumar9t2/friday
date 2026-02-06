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
                // Primary device - small footprint, hardware rendering
                localDevices.maybeCreate("pixel4Api30").apply {
                    device = "Pixel 4"
                    apiLevel = 30
                    systemImageSource = "aosp"
                    testedAbi = "x86_64"
                }
                // minSdk boundary testing (matches app minSdk=28)
                localDevices.maybeCreate("pixel4Api28").apply {
                    device = "Pixel 4"
                    apiLevel = 28
                    systemImageSource = "aosp"
                    testedAbi = "x86_64"
                }
                // Latest API with Play Services
                localDevices.maybeCreate("pixel6Api34").apply {
                    device = "Pixel 6"
                    apiLevel = 34
                    systemImageSource = "google"
                    testedAbi = "x86_64"
                }

                groups.maybeCreate("phone").apply {
                    targetDevices.addAll(localDevices.matching { it.name == "pixel4Api30" })
                }
                groups.maybeCreate("minSdk").apply {
                    targetDevices.addAll(localDevices.matching { it.name == "pixel4Api28" })
                }
                groups.maybeCreate("latest").apply {
                    targetDevices.addAll(localDevices.matching { it.name == "pixel6Api34" })
                }
            }
        }
    }
}
