package es.furynocturn.viaviewer.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Definiciones de item (items/*.json) de 26.3 para clientes de antes de 1.21.9: el contexto
 * "on_shelf" no existe (se quita de los "when") y los modelos especiales nuevos no se pueden leer
 * (esa definicion no se escribe; sus items tampoco estan en el descriptor).
 */
final class ItemDefinitions {

    private static final String SHELF_CONTEXT = "on_shelf";
    /** Modelos especiales de 1.21.9+. */
    private static final Set<String> NEW_SPECIAL_MODELS = Set.of("minecraft:copper_golem_statue");

    private ItemDefinitions() {
    }

    /** La definicion convertida, la misma si no hacia falta, o null si este cliente no la puede leer. */
    static byte[] convert(final byte[] definition) {
        final JsonElement root;
        try {
            root = JsonParser.parseString(new String(definition, StandardCharsets.UTF_8));
        } catch (final RuntimeException e) {
            return definition;
        }
        if (usesNewSpecialModel(root)) {
            return null;
        }
        return removeShelfContext(root) ? root.toString().getBytes(StandardCharsets.UTF_8) : definition;
    }

    private static boolean usesNewSpecialModel(final JsonElement element) {
        if (element.isJsonObject()) {
            final JsonObject object = element.getAsJsonObject();
            final JsonElement type = object.get("type");
            if (type != null && type.isJsonPrimitive() && NEW_SPECIAL_MODELS.contains(type.getAsString())) {
                return true;
            }
            for (final Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if (usesNewSpecialModel(entry.getValue())) {
                    return true;
                }
            }
        } else if (element.isJsonArray()) {
            for (final JsonElement child : element.getAsJsonArray()) {
                if (usesNewSpecialModel(child)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Quita "on_shelf" de cada "when"; un caso que solo era para la estanteria desaparece. */
    private static boolean removeShelfContext(final JsonElement element) {
        boolean changed = false;
        if (element.isJsonObject()) {
            final JsonObject object = element.getAsJsonObject();
            if (object.has("cases") && object.get("cases").isJsonArray()) {
                final Iterator<JsonElement> cases = object.getAsJsonArray("cases").iterator();
                while (cases.hasNext()) {
                    final JsonElement current = cases.next();
                    if (!current.isJsonObject() || !current.getAsJsonObject().has("when")) {
                        continue;
                    }
                    final JsonObject selectCase = current.getAsJsonObject();
                    final JsonElement when = selectCase.get("when");
                    if (when.isJsonPrimitive() && SHELF_CONTEXT.equals(when.getAsString())) {
                        cases.remove();
                        changed = true;
                    } else if (when.isJsonArray()) {
                        final JsonArray values = when.getAsJsonArray();
                        changed |= values.remove(new com.google.gson.JsonPrimitive(SHELF_CONTEXT));
                        if (values.isEmpty()) {
                            cases.remove();
                        }
                    }
                }
            }
            for (final Map.Entry<String, JsonElement> entry : object.entrySet()) {
                changed |= removeShelfContext(entry.getValue());
            }
        } else if (element.isJsonArray()) {
            for (final JsonElement child : element.getAsJsonArray()) {
                changed |= removeShelfContext(child);
            }
        }
        return changed;
    }
}
