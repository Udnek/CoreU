plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("com.gradleup.shadow") version "9.3.0"
}

group = "me.udnek"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven ("https://repo.papermc.io/repository/maven-public/")
    maven ("https://maven.fabricmc.net/")
    //maven ("https://repo.dueris.org/repository/maven-public/")
}

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 11 installed for example.
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")

    compileOnly("space.vectrix.ignite:ignite-api:1.1.0")
    compileOnly("net.fabricmc:sponge-mixin:0.15.2+mixin.0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
}


tasks {
    shadowJar{
        archiveBaseName.set("CoreU")
        archiveClassifier.set("")
        archiveVersion.set(version.toString())
    }
    compileJava {
        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release = 21
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}