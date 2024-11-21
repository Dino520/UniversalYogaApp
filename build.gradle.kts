plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.3.14" apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.14")
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath ("com.android.tools.build:gradle:8.5.1")
    }
}
