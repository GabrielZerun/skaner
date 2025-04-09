// Plik: app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.barcode_scanner"
    compileSdk = 35 

    defaultConfig {
        applicationId = "com.example.barcode_scanner"
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
  
    kapt {
        correctErrorTypes = true
    }
    
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

    skaner ZXing
    implementation(libs.scanner)

    // Zależności Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Wsparcie dla Coroutines
    kapt(libs.androidx.room.compiler)     // Procesor adnotacji dla Kapt

    
    implementation(libs.androidx.lifecycle.runtime.ktx)

   
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.room.testing) // Testowanie Room (opcjonalne)
}
