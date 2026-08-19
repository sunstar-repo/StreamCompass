import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()

    android {
        namespace = "com.sunstar.streamcompass.presentation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        // Works around a known Compose Multiplatform bug (CMP-7611 / CMP-9547): with
        // com.android.kotlin.multiplatform.library + AGP 9, CopyResourcesToAndroidAssetsTask's
        // outputDirectory never gets wired, so composeResources never reach the APK/AAR assets.
        androidResources.enable = true

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.androidx.core.view)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(projects.architecture.domain)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.adaptiveNavigationSuite)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serializationJson)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.paging.common)
            implementation(libs.paging.compose)
            implementation(libs.koin.composeViewmodel)
            implementation(libs.coil.compose)
            implementation(libs.coil.networkKtor3)
            implementation(libs.coil.svg)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
