package es.furynocturn.viaviewer.assets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

/**
 * Baja de Mojang los assets que una version nueva tiene y la nativa no, y los deja en
 * {@code <juego>/fnviaviewer/cache/<version>/} con forma de resource pack. Nada de Mojang va en el jar.
 * Cada parte (client.jar, sonidos, cada idioma) se marca hecha en state.json y no se repite.
 */
public final class AssetCache {

    private static final String MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    private static final String OBJECTS = "https://resources.download.minecraft.net/";
    private static final String PREFIX = "assets/minecraft/";
    private static final List<String> FOLDERS = List.of("blockstates/", "models/", "items/", "textures/", "particles/");
    private static final String BEDROCK_COMMIT = "15701fa9c13923f12662984c32c124327e40e3d3";
    /** {ruta en resource_pack/, SHA-1}. Solo los mobs nuevos para este cliente (1.21.11 los trae todos). */
    private static final String[][] BEDROCK_FILES = {
        //#if MC < 12111
        //$$ {"models/entity/nautilus.geo.json", "6845a53ea120109677d3f25fc419f15e4f602a50"},
        //$$ {"models/entity/nautilus_baby.geo.json", "6446402798d711d5770429cb3ad33cf6e072a5a2"},
        //$$ {"models/entity/zombie_nautilus_coral.geo.json", "fb3f86482620f01af81424030f704bff428ba5ff"},
        //$$ {"models/entity/parched.geo.json", "0e7c16ee4fd0368d40559afb25e9df67593c0b61"},
        //$$ {"animations/nautilus.animation.json", "22b5494926998f487e303840d181d3e58e59c5ae"},
        //#endif
        //#if MC < 12109
        //$$ {"models/entity/copper_golem.geo.json", "1cd794f1f0a3bfb887f961a581445add1f97d808"},
        //$$ {"animations/copper_golem.animation.json", "ce75ed25905fd750c4b90f28dd7dd86dfba275af"},
        //#endif
    };
    /** Sube cuando cambia lo que se saca del client.jar (2: particles/): las caches viejas se rehacen. */
    //#if MC >= 12111
    /** Si el cliente acepta cubos girados mas de 45 grados (desde 1.21.11); si no, ModelRotations los convierte. */
    private static final boolean FREE_ROTATIONS = true;
    //#else
    //$$ private static final boolean FREE_ROTATIONS = false;
    //#endif
    //#if MC >= 12109
    /** Si el cliente conoce lo de las definiciones de item de 1.21.9 (contexto on_shelf...); si no, ItemDefinitions. */
    private static final boolean SHELF_ITEMS = true;
    //#else
    //$$ private static final boolean SHELF_ITEMS = false;
    //#endif
    /** Sube cuando cambia lo que se saca del client.jar: la cache se rehace. 3: giros convertidos; 6: y definiciones de item y paletas. */
    private static final int CLIENT_FORMAT = FREE_ROTATIONS ? 2 : SHELF_ITEMS ? 3 : 6;
    private static final Pattern SAFE_PATH = Pattern.compile("[a-z0-9_./-]+");
    private static final Gson GSON = new Gson();
    private static final org.slf4j.Logger LOGGER = es.furynocturn.viaviewer.via.ViaTranslator.LOGGER;

    private final String version;
    private final String nativeVersion;
    private final Path dir;
    private final HttpClient http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15)).followRedirects(HttpClient.Redirect.NORMAL).build();
    private final Map<String, JsonObject> versionMeta = new HashMap<>();
    private final Map<String, JsonObject> assetIndex = new HashMap<>();
    private JsonObject manifest;

    public AssetCache(final String version, final String nativeVersion) {
        this.version = version;
        this.nativeVersion = nativeVersion;
        this.dir = packDir(version);
    }

    public static Path packDir(final String version) {
        return FabricLoader.getInstance().getGameDir().resolve("fnviaviewer").resolve("cache").resolve(version);
    }

    /** Baja lo que falte. Devuelve true si ha escrito algo nuevo (hay que recargar recursos). */
    public boolean update(final String languageCode) throws IOException, InterruptedException {
        final Path statePath = this.dir.resolve("state.json");
        final JsonObject state = Files.exists(statePath)
            ? JsonParser.parseString(Files.readString(statePath)).getAsJsonObject() : new JsonObject();
        if (!state.has("langs")) {
            state.add("langs", new JsonArray());
        }
        boolean changed = false;

        if (!state.has("client_format") || state.get("client_format").getAsInt() < CLIENT_FORMAT) {
            if (state.has("client_format")) {
                // Formato viejo: fuera todo (quedarian ficheros que este cliente ya no puede leer) y se baja de nuevo
                this.clear();
                state.remove("sounds");
                state.add("langs", new JsonArray());
            }
            this.extractClientJar();
            state.remove("client");
            state.addProperty("client_format", CLIENT_FORMAT);
            save(statePath, state);
            changed = true;
        }
        if (!state.has("sounds")) {
            this.downloadSounds();
            state.addProperty("sounds", true);
            save(statePath, state);
            changed = true;
        }
        if (BEDROCK_FILES.length > 0 && !BEDROCK_COMMIT.equals(state.has("bedrock") ? state.get("bedrock").getAsString() : "")) {
            this.downloadBedrock();
            state.addProperty("bedrock", BEDROCK_COMMIT);
            save(statePath, state);
            changed = true;
        }
        if (SignTextures.generate(this.dir)) {
            changed = true;
        }
        SignTextures.scan(this.dir);
        // en_us viene del client.jar; el idioma del jugador, del indice de assets
        final JsonArray langs = state.getAsJsonArray("langs");
        if (!languageCode.equals("en_us") && !langs.contains(GSON.toJsonTree(languageCode))) {
            this.downloadLanguage(languageCode);
            langs.add(languageCode);
            save(statePath, state);
            changed = true;
        }
        return changed;
    }

    public void clear() throws IOException {
        if (Files.exists(this.dir)) {
            try (var paths = Files.walk(this.dir)) {
                for (final Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }

    private void extractClientJar() throws IOException, InterruptedException {
        final JsonObject client = this.versionMeta(this.version).getAsJsonObject("downloads").getAsJsonObject("client");
        final byte[] jar = this.download(client.get("url").getAsString(), client.get("sha1").getAsString());
        int written = 0;
        final List<byte[]> atlases = new java.util.ArrayList<>();
        final java.util.Set<String> names = new java.util.HashSet<>();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(jar))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                final String name = entry.getName();
                names.add(name);
                if (entry.isDirectory() || !name.startsWith(PREFIX) || !SAFE_PATH.matcher(name).matches() || name.contains("..")) {
                    continue;
                }
                final String path = name.substring(PREFIX.length());
                if (path.startsWith("atlases/") && path.endsWith(".json")) {
                    atlases.add(zip.readAllBytes());
                } else if (path.equals("lang/en_us.json")) {
                    this.writeNewKeys(path, zip.readAllBytes(), vanillaResource(name));
                } else if (FOLDERS.stream().anyMatch(path::startsWith) && Minecraft.class.getResource("/" + name) == null) {
                    final byte[] data = this.convert(path, zip.readAllBytes());
                    if (data == null) {
                        continue;
                    }
                    this.write(name, data);
                    written++;
                }
            }
        }
        for (final Map.Entry<String, byte[]> atlas : TrimPalettes.extensions(TrimPalettes.permutations(atlases, names)).entrySet()) {
            this.write(atlas.getKey(), atlas.getValue());
            written++;
        }
        LOGGER.info("{}: {} ficheros nuevos del client.jar", this.version, written);
    }

    private void downloadSounds() throws IOException, InterruptedException {
        final JsonObject newIndex = this.assetIndex(this.version);
        final JsonObject nativeIndex = this.assetIndex(this.nativeVersion);
        final JsonObject newSounds = this.object(newIndex, "minecraft/sounds.json").getAsJsonObject();
        final JsonObject nativeSounds = this.object(nativeIndex, "minecraft/sounds.json").getAsJsonObject();

        final JsonObject out = new JsonObject();
        int files = 0;
        for (final Map.Entry<String, JsonElement> event : newSounds.entrySet()) {
            if (nativeSounds.has(event.getKey())) {
                continue;
            }
            out.add(event.getKey(), event.getValue());
            final JsonArray sounds = event.getValue().getAsJsonObject().getAsJsonArray("sounds");
            for (final JsonElement sound : sounds == null ? new JsonArray() : sounds) {
                final boolean isEvent = sound.isJsonObject() && sound.getAsJsonObject().has("type")
                    && sound.getAsJsonObject().get("type").getAsString().equals("event");
                if (isEvent) {
                    continue;
                }
                final String soundName = sound.isJsonObject() ? sound.getAsJsonObject().get("name").getAsString() : sound.getAsString();
                final String key = "minecraft/sounds/" + stripNamespace(soundName) + ".ogg";
                if (!nativeIndex.has(key) && newIndex.has(key)) {
                    final String hash = newIndex.getAsJsonObject(key).get("hash").getAsString();
                    this.write("assets/" + key, this.download(OBJECTS + hash.substring(0, 2) + "/" + hash, hash));
                    files++;
                }
            }
        }
        this.write(PREFIX + "sounds.json", GSON.toJson(out).getBytes(StandardCharsets.UTF_8));
        LOGGER.info("{}: {} eventos de sonido nuevos, {} ficheros", this.version, out.size(), files);
    }

    private void downloadLanguage(final String code) throws IOException, InterruptedException {
        final String key = "minecraft/lang/" + code + ".json";
        final JsonObject newIndex = this.assetIndex(this.version);
        if (!SAFE_PATH.matcher(code).matches() || !newIndex.has(key)) {
            return;
        }
        final JsonObject nativeIndex = this.assetIndex(this.nativeVersion);
        final byte[] nativeLang = nativeIndex.has(key) ? this.object(nativeIndex, key).toString().getBytes(StandardCharsets.UTF_8) : null;
        this.writeNewKeys("lang/" + code + ".json", this.object(newIndex, key).toString().getBytes(StandardCharsets.UTF_8), nativeLang);
    }

    /** Escribe solo las claves de idioma que la version nativa no tiene (los idiomas se mezclan entre packs). */
    private void writeNewKeys(final String path, final byte[] newer, final byte[] older) throws IOException {
        final JsonObject newKeys = JsonParser.parseString(new String(newer, StandardCharsets.UTF_8)).getAsJsonObject();
        final JsonObject oldKeys = older == null ? new JsonObject() : JsonParser.parseString(new String(older, StandardCharsets.UTF_8)).getAsJsonObject();
        final JsonObject out = new JsonObject();
        newKeys.entrySet().stream().filter(e -> !oldKeys.has(e.getKey())).forEach(e -> out.add(e.getKey(), e.getValue()));
        this.write(PREFIX + path, GSON.toJson(out).getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] vanillaResource(final String name) throws IOException {
        try (InputStream in = Minecraft.class.getResourceAsStream("/" + name)) {
            return in == null ? null : in.readAllBytes();
        }
    }

    private static String stripNamespace(final String id) {
        final int colon = id.indexOf(':');
        return colon < 0 ? id : id.substring(colon + 1);
    }

    private JsonElement object(final JsonObject index, final String key) throws IOException, InterruptedException {
        final String hash = index.getAsJsonObject(key).get("hash").getAsString();
        return JsonParser.parseString(new String(this.download(OBJECTS + hash.substring(0, 2) + "/" + hash, hash), StandardCharsets.UTF_8));
    }

    private JsonObject versionMeta(final String id) throws IOException, InterruptedException {
        if (!this.versionMeta.containsKey(id)) {
            if (this.manifest == null) {
                this.manifest = JsonParser.parseString(new String(this.download(MANIFEST, null), StandardCharsets.UTF_8)).getAsJsonObject();
            }
            JsonObject found = null;
            for (final JsonElement element : this.manifest.getAsJsonArray("versions")) {
                if (element.getAsJsonObject().get("id").getAsString().equals(id)) {
                    found = element.getAsJsonObject();
                }
            }
            if (found == null) {
                throw new IOException("La version " + id + " no esta en el manifest de Mojang");
            }
            final byte[] meta = this.download(found.get("url").getAsString(), found.get("sha1").getAsString());
            this.versionMeta.put(id, JsonParser.parseString(new String(meta, StandardCharsets.UTF_8)).getAsJsonObject());
        }
        return this.versionMeta.get(id);
    }

    private JsonObject assetIndex(final String id) throws IOException, InterruptedException {
        if (!this.assetIndex.containsKey(id)) {
            final JsonObject info = this.versionMeta(id).getAsJsonObject("assetIndex");
            final byte[] index = this.download(info.get("url").getAsString(), info.get("sha1").getAsString());
            this.assetIndex.put(id, JsonParser.parseString(new String(index, StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("objects"));
        }
        return this.assetIndex.get(id);
    }

    private byte[] download(final String url, final String sha1) throws IOException, InterruptedException {
        if (!url.startsWith("https://")) {
            throw new IOException("URL no segura: " + url);
        }
        final HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofMinutes(2))
            .header("User-Agent", "FNViaViewer").GET().build();
        final HttpResponse<byte[]> response = this.http.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " en " + url);
        }
        if (sha1 != null && !sha1.equalsIgnoreCase(sha1(response.body()))) {
            throw new IOException("SHA-1 incorrecto en " + url);
        }
        return response.body();
    }

    /**
     * Modelos y animaciones de Bedrock (Mojang/bedrock-samples) de los mobs que este cliente no tiene:
     * se dibujan con ellos en lugar del sustituto de ViaBackwards (entity/bedrock). Fijados a un commit
     * y comprobados con su SHA-1.
     */
    private void downloadBedrock() throws IOException, InterruptedException {
        for (final String[] file : BEDROCK_FILES) {
            final byte[] data = this.download("https://raw.githubusercontent.com/Mojang/bedrock-samples/" + BEDROCK_COMMIT
                + "/resource_pack/" + file[0], file[1]);
            this.write("bedrock/" + file[0].substring(file[0].lastIndexOf('/') + 1), data);
        }
        LOGGER.info("{}: {} modelos y animaciones de Bedrock", this.version, BEDROCK_FILES.length);
    }

    /** Un fichero de Bedrock de la cache (models/entity/x.geo.json -> bedrock/x.geo.json). */
    public static Path bedrockFile(final String version, final String name) {
        return packDir(version).resolve("bedrock").resolve(name);
    }

    /** El fichero en el formato de este cliente, o null si no lo puede usar. */
    private byte[] convert(final String path, final byte[] data) {
        if (!path.endsWith(".json")) {
            return data;
        }
        if (!FREE_ROTATIONS && path.startsWith("models/")) {
            return ModelRotations.convert(data);
        }
        if (!SHELF_ITEMS && path.startsWith("items/")) {
            return ItemDefinitions.convert(data);
        }
        return data;
    }

    private void write(final String relative, final byte[] data) throws IOException {
        final Path target = this.dir.resolve(relative).normalize();
        if (!target.startsWith(this.dir)) {
            throw new IOException("Ruta fuera de la cache: " + relative);
        }
        Files.createDirectories(target.getParent());
        Files.write(target, data);
    }

    private static void save(final Path statePath, final JsonObject state) throws IOException {
        Files.createDirectories(statePath.getParent());
        Files.writeString(statePath, GSON.toJson(state));
    }

    private static String sha1(final byte[] data) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(data));
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
