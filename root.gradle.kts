plugins {
    id("gg.essential.loom") version "1.15.50" apply false
    id("gg.essential.multi-version.root")
}

preprocess {
    strictExtraMappings.set(true)

    // Arbol de versiones: cada enlace convierte el codigo de una version a la siguiente.
    // Los nombres de Mojang que cambian entre dos versiones van en versions/<a>-<b>.txt.
    // Se enlaza de la mas nueva a la mas vieja; el fichero de nombres (si hace falta) es versions/<nueva>-<vieja>.txt
    val versions = listOf("26.2" to 26_02_00, "26.1" to 26_01_00, "1.21.11" to 1_21_11, "1.21.10" to 1_21_10, "1.21.8" to 1_21_08)
    val nodes = versions.map { (name, mc) -> name to createNode("$name-fabric", mc, "mojmap") }
    nodes.zipWithNext { (newName, newer), (oldName, older) ->
        val names = file("versions/$newName-$oldName.txt")
        newer.link(older, names.takeIf(File::exists))
    }
}

// Jar universal: los jars de cada version metidos en uno. Fabric carga los mods anidados "si es posible"
// y como mucho uno por id (ModSolver, ModLoadCondition.IF_POSSIBLE), asi que solo entra el que encaja con
// el minecraft y el java del juego. Via va una sola vez, fuera de los jars de cada version.
tasks.register("universalJar") {
    group = "build"
    val versions = listOf("26.2", "26.1", "1.21.11", "1.21.10", "1.21.8")
    val mod = project.property("mod_version") as String
    versions.forEach { dependsOn(":$it-fabric:assemble") }
    val jars = versions.map { file("versions/$it-fabric/build/libs/fnviaviewer-$mod+$it.jar") }
    val out = layout.buildDirectory.file("libs/fnviaviewer-$mod-universal.jar")
    inputs.files(jars)
    outputs.file(out)
    doLast {
        val via = linkedMapOf<String, ByteArray>()
        val inner = linkedMapOf<String, ByteArray>()
        var icon: ByteArray? = null
        var license: ByteArray? = null
        jars.forEach { jar ->
            val buf = java.io.ByteArrayOutputStream()
            java.util.zip.ZipFile(jar).use { zip ->
                java.util.zip.ZipOutputStream(buf).use { zos ->
                    for (e in zip.entries()) {
                        val data = zip.getInputStream(e).use { it.readBytes() }
                        when {
                            e.name.startsWith("META-INF/jars/") -> {
                                if (!e.isDirectory) via.putIfAbsent(e.name, data)
                                continue
                            }
                            e.name == "assets/fnviaviewer/icon.png" -> icon = data
                            e.name == "LICENSE_fnviaviewer" -> license = data
                        }
                        val bytes = if (e.name == "fabric.mod.json") {
                            @Suppress("UNCHECKED_CAST")
                            val json = groovy.json.JsonSlurper().parse(data) as MutableMap<String, Any>
                            json.remove("jars")
                            groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(json)).toByteArray()
                        } else data
                        zos.putNextEntry(java.util.zip.ZipEntry(e.name).apply { time = e.time })
                        zos.write(bytes)
                        zos.closeEntry()
                    }
                }
            }
            inner["META-INF/jars/${jar.name}"] = buf.toByteArray()
        }
        val meta = linkedMapOf(
            "schemaVersion" to 1,
            "id" to "fnviaviewer-universal",
            "version" to mod,
            "name" to "FNViaViewer (universal)",
            "description" to "FNViaViewer for every supported client version in one file. Fabric loads only the one for your game.",
            "authors" to listOf("FuryNocturn"),
            "contact" to mapOf("sources" to "https://github.com/FuryNocturn/FNViaViewer"),
            "license" to "GPL-3.0-only",
            "icon" to "assets/fnviaviewer/icon.png",
            "environment" to "client",
            // Obliga a que entre una de las versiones; si ninguna encaja, Fabric lo dice al arrancar
            "depends" to mapOf("fnviaviewer" to "*", "minecraft" to ">=1.21.7 <=26.2"),
            "jars" to (via.keys + inner.keys).map { mapOf("file" to it) },
        )
        val files = linkedMapOf<String, ByteArray>(
            "fabric.mod.json" to groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(meta)).toByteArray(),
        )
        icon?.let { files["assets/fnviaviewer/icon.png"] = it }
        license?.let { files["LICENSE_fnviaviewer"] = it }
        files.putAll(via)
        files.putAll(inner)
        val target = out.get().asFile
        target.parentFile.mkdirs()
        java.util.zip.ZipOutputStream(target.outputStream()).use { zos ->
            files.forEach { (name, data) ->
                zos.putNextEntry(java.util.zip.ZipEntry(name).apply { time = 315532800000L })
                zos.write(data)
                zos.closeEntry()
            }
        }
    }
}
