plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)


    id("com.google.gms.google-services")
}

android {
    namespace = "com.android.productsaddere_commerce"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.productsaddere_commerce"
        minSdk = 23
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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //firebase fireStore
    implementation ("com.google.firebase:firebase-firestore:25.1.1")
    //firebase cloud storage
    implementation ("com.google.firebase:firebase-storage:21.0.1")

    //Color picker
    implementation ("com.github.skydoves:colorpickerview:2.2.4")

    //Lifecycle
    val lifecycle_version = "2.5.1"
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")

    //Material Design
    implementation ("com.google.android.material:material:1.3.0-alpha03")

}