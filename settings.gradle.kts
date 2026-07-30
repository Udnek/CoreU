pluginManagement {
    plugins {
        kotlin("jvm") version "2.3.0"
    }

    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.canvasmc.io/public")
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "CoreU"