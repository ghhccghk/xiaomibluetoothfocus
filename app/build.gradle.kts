import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.ghhccghk.xiaomibluetoothdiy"
    compileSdk = 35
    val buildTime = System.currentTimeMillis()
    val localProperties = Properties()
    if (rootProject.file("local.properties").canRead())
        localProperties.load(rootProject.file("local.properties").inputStream())

    defaultConfig {
        applicationId = "com.ghhccghk.xiaomibluetoothdiy"
        minSdk = 32
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        dependenciesInfo.includeInApk = false
        ndk.abiFilters += arrayOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64","armeabi","mips", "mips64")
        buildConfigField("long", "BUILD_TIME", "$buildTime")
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val config = localProperties.getProperty("androidStoreFile")?.let {
        signingConfigs.create("config") {
            storeFile = file(it)
            storePassword = localProperties.getProperty("androidStorePassword")
            keyAlias = localProperties.getProperty("androidKeyAlias")
            keyPassword = localProperties.getProperty("androidKeyPassword")
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        all {
            signingConfig = config ?: signingConfigs["debug"]
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
        androidResources.additionalParameters("--allow-reserved-package-id", "--package-id", "0x64")
        aaptOptions.cruncherEnabled = false
        buildFeatures.buildConfig = true
        dependenciesInfo.includeInApk = false
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "**"
        }
        dex {
            useLegacyPackaging = true
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    applicationVariants.all {
        outputs.all {
            (this as BaseVariantOutputImpl).outputFileName = "xiaomibluetoothdiy-$versionName-$versionCode-$name-$buildTime.apk"
        }
    }
}

dependencies {
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.navigation.fragment)
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    compileOnly(libs.xposed)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    implementation(libs.ezxhelper)
    implementation(libs.xkt)
    implementation(libs.dsp)
    implementation(libs.dexkit)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)


    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.materialWindow)
    implementation(libs.androidx.compose.ui.googlefonts)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material3)


    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.hyperfocusapi)
}