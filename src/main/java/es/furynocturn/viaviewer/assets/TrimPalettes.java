package es.furynocturn.viaviewer.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;

/**
 * Colores de adorno de armadura nuevos (copper_darker, de 1.21.9): las texturas con cada color las
 * genera el atlas a partir de su lista de paletas. Por cada atlas vanilla con paletas se escribe uno
 * en la cache solo con las que le faltan; Minecraft junta los atlas de todos los paquetes.
 */
final class TrimPalettes {

    private static final String ATLASES = "assets/minecraft/atlases/";
    private static final String[] VANILLA_ATLASES = {"blocks", "items", "armor_trims"};

    private TrimPalettes() {
    }

    /**
     * Paletas de la version nueva (nombre -> textura), de sus atlas. En 26.x las paletas estan en
     * textures/palettes/ y su nombre no lo lleva; en las viejas el nombre es la ruta desde textures/.
     */
    static Map<String, String> permutations(final List<byte[]> newerAtlases, final java.util.Set<String> newerFiles) {
        final Map<String, String> all = new LinkedHashMap<>();
        for (final byte[] atlas : newerAtlases) {
            for (final JsonObject source : palettedSources(JsonParser.parseString(new String(atlas, StandardCharsets.UTF_8)))) {
                source.getAsJsonObject("permutations").entrySet().forEach(e -> {
                    final String texture = e.getValue().getAsString();
                    final int colon = texture.indexOf(':');
                    final String namespace = colon < 0 ? "minecraft" : texture.substring(0, colon);
                    final String path = texture.substring(colon + 1);
                    final boolean inPalettes = newerFiles.contains("assets/" + namespace + "/textures/palettes/" + path + ".png");
                    all.putIfAbsent(e.getKey(), inPalettes ? namespace + ":palettes/" + path : texture);
                });
            }
        }
        return all;
    }

    /** Los atlas que hay que anadir (ruta -> contenido), con las paletas que le faltan a cada atlas vanilla. */
    static Map<String, byte[]> extensions(final Map<String, String> newer) throws IOException {
        final Map<String, byte[]> out = new LinkedHashMap<>();
        for (final String name : VANILLA_ATLASES) {
            final String path = ATLASES + name + ".json";
            final JsonElement vanilla;
            try (InputStream in = Minecraft.class.getResourceAsStream("/" + path)) {
                if (in == null) {
                    continue;
                }
                vanilla = JsonParser.parseString(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            }
            final JsonArray sources = new JsonArray();
            for (final JsonObject source : palettedSources(vanilla)) {
                final JsonObject missing = new JsonObject();
                newer.forEach((palette, texture) -> {
                    if (!source.getAsJsonObject("permutations").has(palette)) {
                        missing.addProperty(palette, texture);
                    }
                });
                if (!missing.isEmpty()) {
                    final JsonObject extension = source.deepCopy();
                    extension.add("permutations", missing);
                    sources.add(extension);
                }
            }
            if (!sources.isEmpty()) {
                final JsonObject atlas = new JsonObject();
                atlas.add("sources", sources);
                out.put(path, atlas.toString().getBytes(StandardCharsets.UTF_8));
            }
        }
        return out;
    }

    private static List<JsonObject> palettedSources(final JsonElement atlas) {
        final List<JsonObject> out = new ArrayList<>();
        if (atlas.isJsonObject() && atlas.getAsJsonObject().has("sources")) {
            for (final JsonElement source : atlas.getAsJsonObject().getAsJsonArray("sources")) {
                if (source.isJsonObject() && source.getAsJsonObject().has("permutations")
                    && source.getAsJsonObject().get("type").getAsString().endsWith("paletted_permutations")) {
                    out.add(source.getAsJsonObject());
                }
            }
        }
        return out;
    }
}
