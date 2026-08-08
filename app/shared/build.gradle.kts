import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()

    android {
       namespace = "com.sunstar.streamcompass.app.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       compilerOptions {
           jvmTarget = JvmTarget.JVM_17
       }
       withHostTest {}
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(project.dependencies.platform(libs.firebase.bom))
        }
        commonMain.dependencies {
            implementation(projects.architecture.domain)
            implementation(projects.architecture.data)
            implementation(projects.architecture.presentation)
            implementation(libs.koin.compose)
            implementation(libs.compose.runtime)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.firebase.firestore)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
