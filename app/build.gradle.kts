
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    kotlin("kapt")
    alias(libs.plugins.androidx.navigation.safeargs)

    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mithilakshar.mithilapanchang"
    compileSdk = 35

    // Load properties from local.properties
    val localProperties = Properties().apply {
        // Load properties from local.properties file
        load(project.rootProject.file("local.properties").inputStream())
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    defaultConfig {
        applicationId = "com.mithilakshar.mithilapanchang"
        minSdk = 29
        targetSdk = 34
        versionCode = 38
        versionName = "38.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Correcting the type to "String"
            buildConfigField("boolean", "FIREBASE_ANALYTICS_ENABLED", "true")
            buildConfigField("String", "sUrl",  localProperties.getProperty("sUrl"))
            buildConfigField("String", "sK",  localProperties.getProperty("sK")) 
            buildConfigField("String", "os",  localProperties.getProperty("os"))
        }
        debug {
            isMinifyEnabled = false // Set to true if you want to enable code shrinking for debug
            buildConfigField("boolean", "FIREBASE_ANALYTICS_ENABLED", "false")
            buildConfigField("String", "sUrl",  localProperties.getProperty("sUrl"))
            buildConfigField("String", "sK",  localProperties.getProperty("sK"))
            buildConfigField("String", "os",  localProperties.getProperty("os"))
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
        viewBinding = true
        buildConfig = true
    }
}


dependencies {

    implementation("com.onesignal:OneSignal:[5.1.6, 5.1.99]")
    implementation("io.ktor:ktor-client-cio:2.3.4")

    implementation("io.github.jan-tennert.supabase:storage-kt:1.3.2")


    implementation ("com.google.android.gms:play-services-identity:18.0.1")
    implementation ("com.google.android.gms:play-services-auth-api-phone:18.0.1")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)



    implementation (libs.androidx.work.runtime)
    implementation(libs.androidx.lifecycle.common.jvm)
    implementation(libs.androidx.databinding.runtime)

    implementation (libs.picasso)
    implementation (libs.glide)

    implementation (libs.okhttp)

    implementation (libs.okio)


    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.gson)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.androidx.core.animation)
    implementation(libs.androidx.animation.graphics.android)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.room.runtime)

    implementation(libs.play.services.ads.lite)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.transport.api)


    annotationProcessor(libs.androidx.room.room.compiler)
    kapt(libs.androidx.room.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation (libs.androidx.viewpager)
    implementation (libs.androidx.fragment)

    implementation(libs.app.update.ktx)
    implementation(libs.review.ktx)





    implementation (libs.android.lottie)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}