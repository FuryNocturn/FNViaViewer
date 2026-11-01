package es.furynocturn.viaviewer.content;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lo que trae una version nueva y la nativa no (lo genera tools/gen_content.py + FallbackTool).
 * Solo usa Via (sin clases de Minecraft) para poder probarlo fuera del juego.
 * Cada cosa nueva tiene un "indice D": su posicion en blocks (por estado), items o sounds.
 */
public record ContentDescriptor(String version, String nativeVersion, List<BlockDef> blocks, List<Entry> items,
                                List<Entry> sounds, List<Step> steps, int[] fallback) {

    /** properties en el orden del report; firstIndex = indice D de su primer estado. */
    public record BlockDef(String id, int newestId, int firstState, int states, int firstIndex,
                           Map<String, List<String>> properties, Map<String, String> defaults) {
    }

    /** stack/rarity solo en items. */
    public record Entry(String id, int newestId, int stack, String rarity) {
    }

    /** IDs de 'from' que no existen en 'to' -> indice D. */
    public record Step(String from, String to, Int2IntMap blockStates, Int2IntMap blocks, Int2IntMap items, Int2IntMap sounds) {
    }

    public static ContentDescriptor load(final InputStream in) throws IOException {
        final JsonObject json;
        try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        }

        final List<BlockDef> blocks = new ArrayList<>();
        int index = 0;
        for (final JsonElement element : json.getAsJsonArray("blocks")) {
            final JsonObject block = element.getAsJsonObject();
            final Map<String, List<String>> properties = new LinkedHashMap<>();
            if (block.has("properties")) {
                for (final Map.Entry<String, JsonElement> property : block.getAsJsonObject("properties").entrySet()) {
                    final List<String> values = new ArrayList<>();
                    property.getValue().getAsJsonArray().forEach(value -> values.add(value.getAsString()));
                    properties.put(property.getKey(), values);
                }
            }
            final Map<String, String> defaults = new LinkedHashMap<>();
            if (block.has("default")) {
                block.getAsJsonObject("default").entrySet().forEach(e -> defaults.put(e.getKey(), e.getValue().getAsString()));
            }
            final int states = block.get("states").getAsInt();
            blocks.add(new BlockDef(block.get("id").getAsString(), block.get("nid").getAsInt(), block.get("first").getAsInt(),
                states, index, properties, defaults));
            index += states;
        }

        final List<Step> steps = new ArrayList<>();
        for (final JsonElement element : json.getAsJsonArray("steps")) {
            final JsonObject step = element.getAsJsonObject();
            final Int2IntMap states = new Int2IntOpenHashMap();
            for (final JsonElement range : step.getAsJsonArray("blocks")) {
                final JsonArray r = range.getAsJsonArray();
                for (int i = 0; i < r.get(2).getAsInt(); i++) {
                    states.put(r.get(0).getAsInt() + i, r.get(1).getAsInt() + i);
                }
            }
            steps.add(new Step(step.get("from").getAsString(), step.get("to").getAsString(), states,
                pairs(step.getAsJsonArray("block_ids")), pairs(step.getAsJsonArray("items")), pairs(step.getAsJsonArray("sounds"))));
        }

        int[] fallback = null;
        if (json.has("fallback")) {
            final JsonArray array = json.getAsJsonArray("fallback");
            fallback = new int[array.size()];
            for (int i = 0; i < fallback.length; i++) {
                fallback[i] = array.get(i).getAsInt();
            }
        }
        return new ContentDescriptor(json.get("version").getAsString(), json.get("native").getAsString(), blocks,
            entries(json.getAsJsonArray("items")), entries(json.getAsJsonArray("sounds")), steps, fallback);
    }

    public int stateCount() {
        final BlockDef last = this.blocks.getLast();
        return last.firstIndex() + last.states();
    }

    private static Int2IntMap pairs(final JsonArray array) {
        final Int2IntMap map = new Int2IntOpenHashMap();
        array.forEach(pair -> map.put(pair.getAsJsonArray().get(0).getAsInt(), pair.getAsJsonArray().get(1).getAsInt()));
        return map;
    }

    private static List<Entry> entries(final JsonArray array) {
        final List<Entry> list = new ArrayList<>();
        for (final JsonElement element : array) {
            final JsonObject entry = element.getAsJsonObject();
            list.add(new Entry(entry.get("id").getAsString(), entry.get("nid").getAsInt(),
                entry.has("stack") ? entry.get("stack").getAsInt() : 0, entry.has("rarity") ? entry.get("rarity").getAsString() : null));
        }
        return list;
    }
}
