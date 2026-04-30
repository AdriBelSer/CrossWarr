plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.yinya.crosswarr"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yinya.crosswarr"
        minSdk = 31
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

    buildFeatures{
        viewBinding = true
        dataBinding = true

    }
}

dependencies {
//Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.firestore)
    implementation (libs.firebase.messaging)

//Recyclerview
    implementation(libs.recyclerview)
    implementation(libs.androidx.cardview)

//Navigation fragment
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

//Para pintar la imagen de google con imageview
    implementation(libs.picasso)

//Splash screen
    implementation(libs.androidx.core.splashscreen)

//Skeleton
    implementation(libs.shimmer)

//Lotties
    implementation(libs.lottie)

//Fugas de memoria
    debugImplementation(libs.leakcanary.android)

//Tests
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
    debugImplementation (libs.androidx.fragment.testing)
    debugImplementation (libs.androidx.core)
    androidTestImplementation (libs.mockito.android)
    androidTestImplementation (libs.androidx.core)
    androidTestImplementation (libs.androidx.runner)
    androidTestImplementation (libs.androidx.rules)

    implementation(libs.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
}