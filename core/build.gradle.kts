plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

group = "com.github.callmephil"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kspApi)
    implementation(libs.kotlinLogger)
    implementation(libs.kotlinpoet)
    implementation(libs.logback)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}