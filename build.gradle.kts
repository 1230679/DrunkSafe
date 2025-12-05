// build.gradle.kts (project level)

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Plugin de Android Tools
        classpath("com.android.tools.build:gradle:8.1.3")
        // Plugin do Kotlin (Versão 1.9.10 é compatível com Compose)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        // Plugin do Google Services para Firebase
        classpath("com.google.gms:google-services:4.3.15")
    }
}


