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
    signingConfigs {
        create("release") {
        }
    }
    namespace = "com.realsoc.cropngrid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.realsoc.cropngrid"
        minSdk = 21
        targetSdk = 34
        versionCode = 3
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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
    // androidx.* version number
    val navVersion = "2.7.5"
    val roomVersion = "2.6.0"
    val splashScreenVersion = "1.0.1"
    val material3Version = "1.1.2"
    val datastoreVersion = "1.0.0"
    val activityComposeVersion = "1.8.0"
    val lifecycleVersion = "2.6.2"

    val composeBom = "2023.10.01"

    // com.google.* version number
    val gsonVersion = "2.10.1"
    val hiltComposeVersion = "1.1.0"
    val accompagnistVersion = "0.32.0"
    val daggerVersion = "2.48.1"

    val firebaseBom = "32.5.0"

    // third parties version number
    val coilVersion = "2.5.0"
    val lottieVersion = "6.1.0"


    // androidx.*
    implementation("androidx.core:core-splashscreen:$splashScreenVersion")
    implementation("androidx.datastore:datastore-preferences:$datastoreVersion")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.hilt:hilt-navigation-compose:$hiltComposeVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")

    implementation(platform("androidx.compose:compose-bom:$composeBom"))
    implementation("androidx.compose.material3:material3")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBom"))
    debugImplementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    // com.google.*
    implementation("com.google.dagger:hilt-android:$daggerVersion")
    ksp("com.google.dagger:hilt-android-compiler:$daggerVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("com.google.accompanist:accompanist-permissions:$accompagnistVersion")

    releaseImplementation(platform("com.google.firebase:firebase-bom:$firebaseBom"))
    releaseImplementation("com.google.firebase:firebase-analytics-ktx")
    releaseImplementation("com.google.firebase:firebase-crashlytics-ktx")


    // third parties
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    implementation("io.coil-kt:coil-compose:$coilVersion")

    testImplementation("junit:junit:4.13.2")
}
