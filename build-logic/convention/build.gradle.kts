plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ktor.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("localProperties") {
            id = "jarvis.local-properties"
            implementationClass = "LocalPropertiesPlugin"
        }
        register("managedDevices") {
            id = "jarvis.managed-devices"
            implementationClass = "GradleManagedDevicesPlugin"
        }
        register("kmpLibrary") {
            id = "jarvis.kmp-library"
            implementationClass = "KmpLibraryPlugin"
        }
        register("ktorServer") {
            id = "jarvis.ktor-server"
            implementationClass = "KtorServerPlugin"
        }
        register("cliApplication") {
            id = "jarvis.cli-application"
            implementationClass = "CliApplicationPlugin"
        }
    }
}
