import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iOS framework linked by iosApp (see iosApp/project.yml). iOS targets only build on macOS.
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "sharedKit"
            binaryOption("bundleId", "com.ovasta.logisticsapp.shared")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.mp.runtime)
            implementation(libs.compose.mp.foundation)
            implementation(libs.compose.mp.ui)
            implementation(libs.compose.mp.material3)
            implementation(libs.compose.mp.material.icons.core)
            // Res is used by :androidApp too while its screens move here
            api(libs.compose.mp.components.resources)
            implementation(libs.compose.mp.ui.tooling.preview)
            // BackHandler in common code (system back on Android, swipe-back on iOS)
            implementation(libs.compose.mp.ui.backhandler)
            // BaseViewModel; the app's ViewModels extend it
            api(libs.jetbrains.lifecycle.viewmodel)
            // LocalLifecycleOwner for screens that react to resume/pause
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            // Image previews (picked photos as bytes; product images in later screens)
            implementation(libs.coil.compose)
            implementation(libs.kotlinx.coroutines.core)
            // In the API of DateExt (today() returns a LocalDate)
            api(libs.kotlinx.datetime)
            // In the API of the models (JsonElement) and of createHttpClient (HttpClient, Logger)
            api(libs.kotlinx.serialization.json)
            api(libs.ktor.client.core)
            api(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            api(libs.koin.core)
            // Firestore and Auth on Android and iOS; iosApp links the Firebase pods
            api(libs.gitlive.firebase.firestore)
            api(libs.gitlive.firebase.auth)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.coroutines.test)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            // The engine keeps the app's OkHttp disk cache (createAndroidHttpEngine)
            implementation(libs.squareup.okhttp)
            // The session's encrypted DataStore file (createAndroidSessionStore)
            implementation(libs.androidx.datastore.preferences)
            // Image picker: gallery/camera launchers, FileProvider, upright JPEGs (ImageExt)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.exifinterface)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        // Tests that change the JVM default time zone
        androidUnitTest.dependencies {
            implementation(libs.junit)
        }
    }
}

android {
    namespace = "com.ovasta.logisticsapp.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // kotlinx-datetime runs on java.time, which needs desugaring below API 26
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

// Strings (ar default, en), drawables and fonts in src/commonMain/composeResources.
// Public so :androidApp can use Res while its screens move here.
compose.resources {
    publicResClass = true
    packageOfResClass = "com.ovasta.logisticsapp.shared.resources"
    generateResClass = always
}
