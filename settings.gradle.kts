pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://maven.architectury.dev")
        maven("https://maven.fabricmc.net")
        // El Loom de Essential (architectury) arrastra librerias de Forge aunque solo se use Fabric
        maven("https://maven.minecraftforge.net")
    }
    plugins {
        val egtVersion = "0.7.2"
        id("gg.essential.multi-version.root") version egtVersion
    }
}

rootProject.name = "FNViaViewer"

// Una carpeta por version de cliente. El codigo esta en src/ escrito para la version base
// (versions/mainProject); el preprocesador lo convierte para las demas.
listOf(
    "26.2-fabric",
    "26.1-fabric",
    "1.21.11-fabric",
    "1.21.10-fabric",
    "1.21.8-fabric",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}

rootProject.buildFileName = "root.gradle.kts"
