import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    jvm()

    android {
        namespace = "com.sunstar.streamcompass.domain"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Model(Entity), Repository interfaces, UseCase — no dependency on :data/:core.
            // Exception: paging-common (KMP, no Android/UI coupling) for Flow<PagingData<T>> in Repository contracts.
            implementation(libs.paging.common)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
