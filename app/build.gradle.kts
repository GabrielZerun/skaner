// Plik: app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Zastosuj wtyczkę Kapt używając jej ID
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.barcode_scanner"
    compileSdk = 35 // Upewnij się, że SDK jest aktualne

    defaultConfig {
        applicationId = "com.example.barcode_scanner"
        minSdk = 24
        targetSdk = 35 // Upewnij się, że SDK jest aktualne
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
    // Konfiguracja dla Kapt
    kapt {
        correctErrorTypes = true
    }
    // Włączenie ViewBinding (opcjonalne, ale przydatne)
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Podstawowe zależności AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Zależność skanera ZXing
    implementation(libs.scanner)

    // Zależności Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Wsparcie dla Coroutines
    kapt(libs.androidx.room.compiler)     // Procesor adnotacji dla Kapt

    // Zależności Lifecycle (dla lifecycleScope)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Zależności testowe
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.room.testing) // Testowanie Room (opcjonalne)
}