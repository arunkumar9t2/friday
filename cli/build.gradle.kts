plugins {
    id("jarvis.cli-application")
    alias(libs.plugins.kotlin.compose)
}

application {
    mainClass.set("dev.arunkumar.jarvis.cli.MainKt")
}

dependencies {
    implementation(project(":shared:models"))
    implementation(libs.clikt)
    implementation(libs.mosaic.runtime)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(kotlin("test"))
}
