plugins {
    kotlin("jvm")
}

group = "com.github.callmephil1"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.libgdx.platform)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}