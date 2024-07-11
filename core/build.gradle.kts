import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    kotlin("jvm")
    //id("com.google.devtools.ksp")
}

group = "com.github.callmephil"
version = "0.0.7"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kspApi)
    implementation(libs.kotlinpoet)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlin("test"))
    api(kotlin("reflect"))
}

tasks.jar {
    archivesName = "parsecs-core"
}

tasks.test {
    useJUnitPlatform()
}

//kotlin {
//    jvmToolchain(19)
//}