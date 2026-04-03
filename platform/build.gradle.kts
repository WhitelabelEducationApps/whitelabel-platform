plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
}

group = "com.whitelabel"
version = "0.1.0"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "WhitelabelPlatform"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.whitelabel:core:0.1.0")
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.core)
                implementation(libs.kermit)
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)
                implementation(libs.ktor.client.core)
                implementation(libs.androidx.navigation.compose)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.driver.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.logging)
                implementation(libs.koin.android)
                implementation(libs.koin.compose)
                implementation(libs.maps.compose)
                implementation(libs.play.services.maps)
                implementation(libs.maps.compose.utils)
                implementation("androidx.palette:palette:1.0.0")
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.sqldelight.driver.native)
            }
        }

        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
    }
}

android {
    namespace = "com.whitelabel.platform"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
