plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.latihansatu"

    // Konfigurasi SDK Android 16 (API 36) terbaru
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.latihansatu"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Pustaka Inti & UI (Dikelola otomatis oleh Version Catalog)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // Pengujian (Testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Jaringan: Retrofit 3.0 (Versi terbaru berbasis Kotlin penuh)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Asinkron: Kotlinx Coroutines Android
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")

    // Arsitektur: Lifecycle ViewModel KTX Terbaru
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.0")

    // Layanan Lokasi: Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // delegasi viemodels()
    implementation("androidx.activity:activity:1.13.0")
    implementation("androidx.activity:activity-ktx:1.13.0")

}
