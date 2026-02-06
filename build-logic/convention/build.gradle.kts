plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
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
    }
}
