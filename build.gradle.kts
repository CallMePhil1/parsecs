plugins {
    kotlin("jvm")
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.23"))
    }
}

group = "com.github.callmephil"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}