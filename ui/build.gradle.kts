plugins {
    kotlin("jvm")
}

group = "com.github.callmephil1"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.libgdx.lwjgl3)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}