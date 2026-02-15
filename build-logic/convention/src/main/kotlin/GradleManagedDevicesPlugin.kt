import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File

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

        // Register task to fix DNS for GMD emulators on Linux with systemd-resolved.
        // On Arch Linux (and similar), /etc/resolv.conf points to 127.0.0.53 which is
        // unreachable from the emulator's network namespace.
        // This config.ini fix works for cold boots; for running emulators, the host's
        // /etc/resolv.conf must be fixed (see gmd-notes.md for details).
        val fixGmdDnsTask = target.tasks.register("fixGmdDns") {
            group = "android"
            description = "Configure DNS for Gradle Managed Devices to fix network issues on Linux"

            doLast {
                val avdDir = File(System.getProperty("user.home"), ".config/.android/avd/gradle-managed")
                if (!avdDir.exists()) {
                    println("GMD AVD directory not found at ${avdDir.absolutePath}")
                    return@doLast
                }

                avdDir.listFiles()?.filter { it.isDirectory && it.name.endsWith(".avd") }?.forEach { avd ->
                    val configFile = File(avd, "config.ini")
                    if (configFile.exists()) {
                        val config = configFile.readText()
                        if (!config.contains("net.dns1")) {
                            configFile.appendText("\n# Google DNS servers (fixes DNS on Linux with systemd-resolved)\n")
                            configFile.appendText("net.dns1=8.8.8.8\n")
                            configFile.appendText("net.dns2=8.8.4.4\n")
                            println("Added DNS config to ${avd.name}")
                        }
                    }
                }
            }
        }

        // Make DNS fix run after pixel6Api36Setup creates the AVD
        target.tasks.configureEach {
            if (name == "pixel6Api36Setup") {
                finalizedBy(fixGmdDnsTask)
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
