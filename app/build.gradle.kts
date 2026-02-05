plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
}

android {
  namespace = "dev.arunkumar.jarvis"
  compileSdk = 36

  defaultConfig {
    applicationId = "dev.arunkumar.jarvis"
    minSdk = 28
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    // TickTick API configuration
    val ticktickApiKey: String = project.findProperty("TICKTICK_API_KEY") as? String ?: ""
    buildConfigField("String", "TICKTICK_API_KEY", "\"$ticktickApiKey\"")
    buildConfigField("String", "TICKTICK_API_BASE_URL", "\"https://ticktick-proxy-j5wtc3hzxq-uc.a.run.app/\"")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
}

ksp {
  arg("circuit.codegen.mode", "hilt") // or "kotlin_inject_anvil"
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.text.google.fonts)

  // Hilt dependencies
  implementation(libs.hilt.android)
  ksp(libs.hilt.android.compiler)
  implementation(libs.hilt.navigation.compose)

  // Circuit dependencies
  implementation(libs.circuit.foundation)
  implementation(libs.circuit.runtime)
  implementation(libs.circuit.runtime.ui)
  implementation(libs.circuit.overlay)
  implementation(libs.circuit.backstack)
  implementation(libs.circuit.codegen.annotations)
  ksp(libs.circuit.codegen)

  // Termux integration dependencies
  implementation(libs.termux.shared)
  implementation(libs.guava.listenablefuture)

  // Room dependencies
  implementation(libs.room.runtime)
  implementation(libs.room.ktx)
  ksp(libs.room.compiler)

  // Retrofit and networking dependencies
  implementation(libs.retrofit)
  implementation(libs.okhttp)
  implementation(libs.okhttp.logging)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.retrofit.kotlinx.serialization)

  // WorkManager dependencies
  implementation(libs.work.runtime.ktx)
  implementation(libs.hilt.work)
  ksp(libs.hilt.work.compiler)

  // Test dependencies
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
