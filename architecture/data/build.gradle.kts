import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

buildkonfig {
    packageName = "com.sunstar.streamcompass.data"

    defaultConfigs {
        buildConfigField(STRING, "TMDB_API_KEY", "5aebc223c62db410f22372277c609140")
        buildConfigField(STRING, "SA_API_KEY", "")
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    jvm()

    android {
        namespace = "com.sunstar.streamcompass.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.architecture.domain)
            implementation(libs.ktor.clientCore)
            implementation(libs.ktor.clientContentNegotiation)
            implementation(libs.ktor.serializationKotlinxJson)
            implementation(libs.kotlinx.serializationJson)
            implementation(libs.koin.core)
            implementation(libs.paging.common)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.firebase.firestore)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.ktor.clientOkhttp)
            implementation(project.dependencies.platform(libs.firebase.bom))
        }
        jvmMain.dependencies {
            implementation(libs.ktor.clientCio)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspJvm", libs.room.compiler)
}
