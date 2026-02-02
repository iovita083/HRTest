plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "jp.slade.heartrate"
    //noinspection GradleDependency
    compileSdk = 32

    defaultConfig {
        applicationId = "jp.slade.heartrate"
        minSdk = 23
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 30
        versionCode = 12
        versionName = "1.2"

    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
}