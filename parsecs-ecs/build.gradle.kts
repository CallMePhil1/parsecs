plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

group = "com.github.callmephil"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":parsecs-ksp"))
    ksp(project(":parsecs-ksp"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}