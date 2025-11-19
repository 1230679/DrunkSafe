import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// usa uma versão do Ktor compatível com o resto do projeto
val ktorVersion = "2.3.8"

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose) apply false
    // id("com.google.gms.google-services") // aplica no project-level se estiveres a usar pluginManagement
}

android {
    namespace = "com.example.drunksafe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.drunksafe"
        minSdk = 24
        targetSdk = 34
    }

    buildFeatures {
        compose = true
    }

    // força um Compose Compiler compatível com Kotlin 1.9.x
    composeOptions {
        // Compose Compiler compatível com Kotlin 1.9.x (1.5.x series)
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    // garante que javac usa Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // kotlinOptions aqui é suportado pelo plugin Android Kotlin
    kotlinOptions {
        jvmTarget = "17"
    }
}

// (opcional mas recomendado) usar toolchain para Kotlin
kotlin {
    jvmToolchain {
        (17)
    }
}

// garante que todas as tasks Kotlin compilam para a mesma JVM target
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // --- CORE ANDROID ---
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- COMPOSE (BOM para alinhar versões) ---
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material") // Material 2
    implementation("androidx.compose.material3:material3") // Material 3

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // --- FIREBASE ---
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    //implementation("com.google.firebase:firebase-crashlytics")

    // --- KTOR & SERIALIZATION ---
    // usa OkHttp engine para Android
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("io.ktor:ktor-client-cio:2.3.8")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}

// Aplica o plugin google services apenas se tiveres a classpath configurada no project-level
apply(plugin = "com.google.gms.google-services")