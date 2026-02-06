plugins {
    id("jarvis.ktor-server")
}

application {
    mainClass.set("dev.arunkumar.jarvis.server.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("server.jar")
    }
}

dependencies {
    implementation(project(":shared:models"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.logback.classic)
    implementation(libs.firebase.admin)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(kotlin("test"))
}
