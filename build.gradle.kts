plugins {
    id("java")
}

group = "de.wattestaebchen"
version = "INDEV-1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}


// Location of developers plugins directory in gradle.properties.
val spigotPluginsDir: String? by project

tasks {
    // This allows you to install your plugin using gradle installPlugin
    task<Copy>("installPlugin") {
        from(jar)
        into(spigotPluginsDir ?: error("Please set spigotPluginsDir in gradle.properties"))
    }
}