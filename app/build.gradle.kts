plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("com.google.dagger.hilt.android") // ✅ correct

}

android {
    namespace = "com.wingspan.loanapp"
    compileSdk = 36
    buildFeatures {
        compose = true
        buildConfig = true // ✅ Enable BuildConfig generation
    }
    defaultConfig {
        applicationId = "com.wingspan.loanapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}
kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.androidx.material.icons.extended)
//navigation
    implementation(libs.androidx.navigation.compose)
    // Hilt
    implementation(libs.hilt.android)

    kapt(libs.hilt.compiler)

    // Hilt + Jetpack Compose Navigation
    implementation(libs.androidx.hilt.navigation.compose)

    // ViewModel + Kotlin Coroutines support
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly (libs.junit.jupiter.engine)
    testImplementation (libs.junit.jupiter.params)

    testImplementation(libs.junit.jupiter.api.v5100)
    testRuntimeOnly(libs.junit.jupiter.engine.v5100)
    testImplementation(libs.junit.jupiter.params.v5100)
    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)
    implementation(libs.retrofit)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // JUnit 5
    testImplementation(libs.junit.jupiter.api)
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.12.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    // Mockito core
    testImplementation("org.mockito:mockito-core:5.5.0")
    // Mockito Kotlin helpers
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")

    // Mockito JUnit 5 integration
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")

    // JUnit 5
    testImplementation(libs.junit.jupiter.api)        // JUnit 5 API
    testImplementation(libs.junit.jupiter.params)     // Parameterized tests
    testRuntimeOnly(libs.junit.jupiter.engine)        // JUnit 5 runtime engine

    // Mockito for JUnit 5 (no inline engine to avoid resolution issues)
    testImplementation(libs.mockito.core.v550)
    testImplementation(libs.mockito.kotlin.v510)           // Kotlin helpers
    testImplementation(libs.mockito.core)       // core
    testImplementation(libs.mockito.kotlin)  // Kotlin helpers
    // Optional AndroidX testing for Android unit/instrumentation tests
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test")

//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(kotlin("test"))
}