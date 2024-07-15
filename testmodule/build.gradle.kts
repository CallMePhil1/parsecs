plugins {
    kotlin("jvm")
    //id("com.google.devtools.ksp")
}

group = "com.github.callmephil"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinLogger)
    //ksp(project(":core"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}