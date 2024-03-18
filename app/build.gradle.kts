plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    //Added Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.guvenlipati"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.guvenlipati"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.exifinterface)
    //Added Firebase Messaging
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Added CircleImageView Library
    implementation(libs.circleimageview)
    //Added Firebase
    implementation(platform(libs.firebase.bom))
    //Glide
    implementation(libs.glide)
    //Lottie
    implementation(libs.lottie)
    //OkHttpClient
    implementation(libs.okhttp)
    //Retrofit
    implementation(libs.retrofit)
    //Gson
    implementation(libs.retrofit2.converter.gson)
    //Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
}