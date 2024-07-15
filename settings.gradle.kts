val kspVersion: String by settings
val kotlinVersion: String by settings

pluginManagement {
    val kspVersion: String by settings
    val kotlinVersion: String by settings

    plugins {
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
    }

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    val libgdxVersion = "1.12.1"

    versionCatalogs {
        create("libs") {
            library("libgdx-gdx", "com.badlogicgames.gdx", "gdx").version(libgdxVersion)
            library("libgdx-lwjgl3", "com.badlogicgames.gdx", "gdx-backend-lwjgl3").version(libgdxVersion)
            library("libgdx-platform", "com.badlogicgames.gdx", "gdx-platform").version(libgdxVersion)
            library("logback", "ch.qos.logback", "logback-classic").version("1.5.6")
            library("kotlinLogger", "io.github.oshai", "kotlin-logging-jvm").version("5.1.0")
            library("kspApi", "com.google.devtools.ksp", "symbol-processing-api").version("1.9.23-1.0.20")
            library("kotlinpoet","com.squareup", "kotlinpoet-ksp").version("1.17.0")
        }
    }
}

rootProject.name = "parsecs"
include("testmodule")
include("core")
include("cosmos")
include("cosmos:core")
