import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val ktorVersion = "2.3.8"

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.drunksafe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.drunksafe"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        val mapsApiKey = project.rootProject.file("local.properties").let {
            if (it.exists()) {
                val properties = Properties()
                properties.load(it.inputStream())
                properties.getProperty("MAPS_API_KEY") ?: ""
            } else {
                ""
            }
        }

        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // ==========================================================
    // 1. CORE ANDROID & LIFECYCLE
    // ==========================================================
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // ==========================================================
    // 2. JETPACK COMPOSE (UI)
    // ==========================================================
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Material Design 2 & 3
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")

    // Ícones
    implementation("androidx.compose.material:material-icons-extended")

    // Navegação
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.maps.android:android-maps-utils:3.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // ==========================================================
    // 3. GOOGLE MAPS & LOCATION
    // ==========================================================
    // Maps Compose (Biblioteca oficial para usar mapas no Compose)
    implementation("com.google.maps.android:maps-compose:4.3.0")

    // Google Maps SDK (Base)
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Location Services (Para obter o GPS/FusedLocation)
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // ==========================================================
    // 4. NETWORKING (RETROFIT & GSON) - ADICIONADO AGORA
    // ==========================================================
    // Retrofit (Para chamar a API de Direções do Google)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Conversor GSON (Para transformar o JSON da API em objetos Kotlin)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // ==========================================================
    // 5. NETWORKING (KTOR)
    // ==========================================================
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // ==========================================================
    // 6. FIREBASE
    // ==========================================================
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    // implementation("com.google.firebase:firebase-crashlytics")

    // ==========================================================
    // 7. ASSYNC & OUTROS
    // ==========================================================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ==========================================================
    // 8. TESTES
    // ==========================================================
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.10")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")

    // Compose Debugging
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}