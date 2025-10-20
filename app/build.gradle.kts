plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.sentinal.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sentinal.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // ✅ Opt-in to experimental APIs globally so you don't edit every screen
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api"
            // If you later see similar errors for foundation/animation, you can add:
            // "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            // "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi"
        )
        // If you ever get "warnings as errors" behavior unintentionally, uncomment:
        // allWarningsAsErrors = false
    }

    buildFeatures { compose = true }

    packaging {
        resources.excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    }
}

dependencies {
    // Compose BOM to align versions
    val bom = platform("androidx.compose:compose-bom:2024.09.01")
    implementation(bom)
    androidTestImplementation(bom)

    // Core + lifecycle + activity
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

    // Compose UI + Material3 (Compose)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.foundation:foundation")

    // ✅ MDC for XML theme parent Theme.Material3.*
    implementation("com.google.android.material:material:1.12.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.2")

    // DataStore (optional)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Location / Geofencing
    implementation("com.google.android.gms:play-services-location:21.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
