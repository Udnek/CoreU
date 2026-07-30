// Settings

group = "me.udnek"
version = "1.0-SNAPSHOT"
var javaVersion = 25
var minecraftVersion = "26.2"

//////////

plugins {
    `java-library`
    kotlin("jvm") version "2.4.0"
    id("org.openrewrite.rewrite") version ("latest.release") // for annotation migration

    id("io.canvasmc.weaver.userdev") version "2.4.5"
    id("io.canvasmc.horizon") version "1.0.2"
}

horizon {
    splitPluginSourceSets()

    accessTransformerFiles.from(
        file("src/main/resources/coreu_at.cfg")
    )
}

rewrite {
    activeRecipe("org.openrewrite.java.jspecify.MigrateFromJetbrainsAnnotations")
    isExportDatatables = true
}

kotlin {
    jvmToolchain(javaVersion)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
}


dependencies {
    horizon.horizonApi("1.0.0.+")
    paperweight.paperDevBundle("$minecraftVersion.build.+")

    includeLibrary("org.jspecify:jspecify:1.0.0")

    "pluginImplementation"("net.dmulloy2:ProtocolLib:5.4.0")
    //compileOnly("net.fabricmc:sponge-mixin:0.15.2+mixin.0.8.7")
    //compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")

    includeLibrary("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    includeLibrary(kotlin("stdlib-jdk8"))

    rewrite("org.openrewrite.recipe:rewrite-migrate-java:3.39.0")
}

tasks {
    compileJava {
        options.release = javaVersion
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}