plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.realsoc.cropngrid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.realsoc.cropngrid"
        minSdk = 23
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // androidx.* version number
    val navVersion = "2.9.2"
    val roomVersion = "2.7.2"
    val splashScreenVersion = "1.0.1"
    val material3Version = "1.3.2"
    val datastoreVersion = "1.1.7"
    val activityComposeVersion = "1.10.1"
    val lifecycleVersion = "2.9.2"

    val composeBom = "2025.07.00"

    // com.google.* version number
    val gsonVersion = "2.11.0"
    val hiltComposeVersion = "1.2.0"
    val accompagnistVersion = "0.37.3"
    val daggerVersion = "2.56.2"

    val firebaseBom = "34.0.0"

    // third parties version number
    val coilVersion = "2.7.0"
    val lottieVersion = "6.3.0"


    // androidx.*
    implementation("androidx.core:core-splashscreen:$splashScreenVersion")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
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

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
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
    releaseImplementation("com.google.firebase:firebase-analytics")
    releaseImplementation("com.google.firebase:firebase-crashlytics")


    // third parties
    implementation("com.airbnb.android:lottie-compose:$lottieVersion")

    implementation("io.coil-kt:coil-compose:$coilVersion")

    testImplementation("junit:junit:4.13.2")
}
