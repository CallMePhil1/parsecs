plugins {
    kotlin("jvm")
}

group = "com.github.callmephil1"
version = "0.0.1"

repositories {
    mavenLocal()
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(project(":core"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}