//plugins {
//    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
//}

pluginManagement {
    val kspVersion: String by settings
    val kotlinVersion: String by settings

    plugins {
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("kotlinLogger", "io.github.oshai", "kotlin-logging-jvm").version("5.1.0")
            library("kspApi", "com.google.devtools.ksp", "symbol-processing-api").version("1.9.23-1.0.20")
            library("logback", "ch.qos.logback", "logback-classic").version("1.5.6")
            library("kotlinpoet","com.squareup", "kotlinpoet-ksp").version("1.17.0")
        }
    }
}

rootProject.name = "parsecs"
include("parsecs-ecs")
include("parsecs-ksp")
