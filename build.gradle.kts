plugins {
    java
}

group = "de.wattestaebchen"
version = "INDEV-1.2.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}


// Location of developers plugins directory in gradle.properties.
val spigotPluginsDir: String? by project

tasks {
    // This allows you to install your plugin using gradle installPlugin
    register<Copy>("installPlugin") {
        from(jar)
        into(spigotPluginsDir ?: error("Please set spigotPluginsDir in gradle.properties"))
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}