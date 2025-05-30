plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.bookreadingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bookreadingapp"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("io.coil-kt:coil-compose:2.1.0")
    implementation ("androidx.navigation:navigation-compose:2.5.3")
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    implementation("androidx.compose.foundation:foundation-layout-android:1.5.4")
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.navigation.testing)
    annotationProcessor("androidx.room:room-compiler:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    testImplementation(libs.junit)
    testImplementation(libs.androidx.ui.test.junit4.android)
    testImplementation(libs.androidx.ui.test.android)
    testImplementation(libs.androidx.ui.test.android)
    testImplementation(libs.androidx.ui.test.junit4.android)
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-inline:5.7.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation("org.mockito:mockito-android:5.7.0")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.mohamedrejeb.ksoup:ksoup-html:0.4.0")
    implementation("com.mohamedrejeb.ksoup:ksoup-entities:0.4.0")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

}