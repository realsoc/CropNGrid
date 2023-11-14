plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.realsoc.cropngrid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.realsoc.cropngrid"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val lottieVersion = "6.1.0"
    val daggerVersion = "2.48.1"
    val navVersion = "2.7.5"
    val roomVersion = "2.6.0"
    val composeBom = "2023.03.00"
    val coilVersion = "2.5.0"
    val gsonVersion = "2.10.1"
    val firebaseBom = "32.4.0"
    val lifecycleVersion = "2.6.2"
    val hiltComposeVersion = "1.1.0"
    val accompagnistVersion = "0.32.0"
    val androidKtxVersion = "1.12.0"
    val splashScreenVersion = "1.0.1"

    implementation("androidx.appcompat:appcompat:1.6.1")


    implementation("io.coil-kt:coil-compose:$coilVersion")

    implementation("com.google.dagger:hilt-android:$daggerVersion")
    ksp("com.google.dagger:hilt-android-compiler:$daggerVersion")

    implementation("com.airbnb.android:lottie-compose:$lottieVersion")


    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")

    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("com.google.code.gson:gson:$gsonVersion")

    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.room:room-ktx:$roomVersion")

    implementation("androidx.hilt:hilt-navigation-compose:$hiltComposeVersion")

    implementation("androidx.navigation:navigation-compose:$navVersion")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    implementation(platform("com.google.firebase:firebase-bom:$firebaseBom"))

    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    implementation("com.google.accompanist:accompanist-permissions:$accompagnistVersion")
    implementation("androidx.core:core-ktx:$androidKtxVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:$composeBom"))
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBom"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // Todo : use ?
    implementation("com.chargemap.compose:numberpicker:1.0.3")
    implementation("androidx.core:core-splashscreen:$splashScreenVersion")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
