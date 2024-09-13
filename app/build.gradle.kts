plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    kotlin("kapt")
    alias(libs.plugins.androidx.navigation.safeargs)
}

android {
    namespace = "com.mithilakshar.mithilapanchang"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mithilakshar.mithilapanchang"
        minSdk = 29
        targetSdk = 34
        versionCode = 19
        versionName = "19.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{viewBinding=true}
}

dependencies {


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


    annotationProcessor(libs.androidx.room.room.compiler)
    kapt(libs.androidx.room.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation (libs.androidx.viewpager)
    implementation (libs.androidx.fragment)

    implementation(libs.app.update.ktx)
    implementation(libs.review.ktx)


    implementation(libs.billing)


    implementation (libs.android.lottie)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}