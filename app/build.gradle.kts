plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.voicesense"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.voicesense"
        minSdk = 24
        targetSdk = 35
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
    }
}

dependencies {

    // dependencies block in app/build.gradle.kts

    dependencies {
        // BOM for Compose
        implementation(platform("androidx.compose:compose-bom:2024.10.01"))

        // Compose UI and Material3
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.material3:material3")
        implementation("androidx.compose.ui:ui-tooling-preview")

        // Debug tools
        debugImplementation("androidx.compose.ui:ui-tooling")

        // Navigation
        implementation("androidx.navigation:navigation-compose:2.8.3")

        // Lifecycle and ViewModel
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

        // Coil (Image Loading)
        implementation ("io.coil-kt:coil-compose:2.7.0")
    }

}