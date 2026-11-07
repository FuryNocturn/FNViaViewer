import gg.essential.gradle.util.noServerRunConfigs

// Un solo build para todas las versiones de cliente (versions/<mc>-fabric). Montaje de
// essential-gradle-toolkit, como EMF y ETF: el codigo de src/ esta escrito para la version base y
// el preprocesador (comentarios //#if MC >= ...) lo convierte para las demas.
plugins {
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

val mcVersion = platform.mcVersion
val modVersion = rootProject.property("mod_version").toString()
val viaVersion = rootProject.property("via_version").toString()

version = "$modVersion+${platform.mcVersionStr}"
group = rootProject.property("maven_group").toString()
base.archivesName.set("fnviaviewer")

/**
 * Por version de cliente: Fabric API, Mod Menu, la version de Minecraft que pide fabric.mod.json y la
 * carpeta de content/ con su descriptor (la version cuyos reports usa tools/gen_content.py).
 */
data class Target(val fabricApi: String, val modMenu: String, val minecraft: String, val content: String = platform.mcVersionStr)

val target = when (mcVersion) {
    26_02_00 -> Target("0.161.0+26.2", "20.0.3", "26.2")
    26_01_00 -> Target("0.145.1+26.1", "18.0.2", ">=26.1 <=26.1.2", "26.1.2")
    1_21_11 -> Target("0.141.6+1.21.11", "17.0.1", "1.21.11")
    // 1.21.9 y 1.21.10 tienen el mismo protocolo
    1_21_10 -> Target("0.138.4+1.21.10", "16.0.1", ">=1.21.9 <=1.21.10")
    // 1.21.7 y 1.21.8 tienen el mismo protocolo
    1_21_08 -> Target("0.136.1+1.21.8", "15.0.2", ">=1.21.7 <=1.21.8")
    else -> throw IllegalStateException("Version sin configurar: $mcVersion")
}

val javaRelease = if (mcVersion >= 26_00_00) 25 else 21

repositories {
    maven("https://repo.viaversion.com")
    maven("https://maven.terraformersmc.com/releases")
    mavenCentral()
}

dependencies {
    modImplementation("net.fabricmc.fabric-api:fabric-api:${target.fabricApi}")
    // Opcional: solo para la pantalla de config en la lista de mods
    modCompileOnly("com.terraformersmc:modmenu:${target.modMenu}")
    // Anotaciones @Nullable: Minecraft las trae desde 1.21.11; antes hay que ponerlas
    compileOnly("org.jspecify:jspecify:1.0.0")

    // Via va dentro del jar (jar-in-jar)
    implementation("com.viaversion:viaversion-common:$viaVersion")
    implementation("com.viaversion:viabackwards-common:$viaVersion")
    include("com.viaversion:viaversion-common:$viaVersion")
    include("com.viaversion:viabackwards-common:$viaVersion")
}

loom.noServerRunConfigs()

// Herramientas de desarrollo (no van en el jar). Ver tools/gen_content.py
// src/tools/java: el preprocesador lo convierte para cada version igual que src/main
val tools: SourceSet by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output + sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().output + sourceSets.main.get().runtimeClasspath
}

tasks.register<JavaExec>("fallbackTool") {
    description = "Calcula los sustitutos con Via y comprueba el parche de tablas"
    classpath = tools.runtimeClasspath
    mainClass.set("es.furynocturn.viaviewer.tools.FallbackTool")
    args(rootProject.file("content/${target.content}/26.3.json").absolutePath,
        layout.buildDirectory.dir("via-tool").get().asFile.absolutePath)
}

tasks.processResources {
    // El descriptor de contenido de este cliente (tools/gen_content.py + fallbackTool)
    from(rootProject.file("content/${target.content}")) { into("fnviaviewer/content") }
    inputs.property("version", modVersion)
    inputs.property("minecraft", target.minecraft)
    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to modVersion, "minecraft" to target.minecraft, "java" to javaRelease))
    }
    filesMatching("fnviaviewer.mixins.json") {
        filter { line -> if (skippedMixins.any { line.trim().startsWith("\"$it\"") }) "" else line }
    }
}

// Lo que una version ya trae de serie no se compila ni se mete en sus mixins:
// - las crias de 26.x, desde 26.1;
// - la tabla del cartel como modelo de bloque, desde 26.2 (la textura del editor y el renderer viejo).
// Y lo que una version aun no tiene, tampoco.
val babies = mcVersion < 26_01_00
val babyMixins = listOf("EntityRendererContextAccessor", "EntityRenderersAccessor", "MixinLivingEntityRenderer",
    "MixinRenderTypes", "ModelPartAccessor")
val skippedMixins = (if (babies) emptyList() else babyMixins) +
    (if (mcVersion >= 26_02_00) listOf("MixinSheets", "MixinAbstractSignRenderer") else emptyList()) +
    // Antes de 1.21.9 el contexto de los renderers no lleva atlas
    (if (mcVersion < 1_21_09) listOf("EntityRendererContextAccessor") else emptyList())

tasks.withType<JavaCompile>().configureEach {
    if (!babies) {
        exclude("es/furynocturn/viaviewer/entity/baby/**")
    }
    // Mobs de 1.21.9-1.21.11 dibujados con datos de Bedrock: desde 1.21.11 son vanilla
    if (mcVersion >= 1_21_11) {
        exclude("es/furynocturn/viaviewer/entity/bedrock/**")
    }
    skippedMixins.forEach { exclude("es/furynocturn/viaviewer/mixin/$it.java") }
    options.encoding = "UTF-8"
    options.release.set(javaRelease)
}

java {
    withSourcesJar()
}

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_fnviaviewer" }
    }
}
