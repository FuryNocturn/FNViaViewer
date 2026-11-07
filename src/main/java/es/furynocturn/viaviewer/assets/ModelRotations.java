package es.furynocturn.viaviewer.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Hasta 1.21.10 los cubos de un modelo solo giran entre -45 y 45 grados; 26.3 usa mas (carteles a
 * -67.5, cadenas de los colgantes a -90). Se pasa el giro sobrante a la geometria: cada cuarto de
 * vuelta mueve las esquinas y los nombres de las caras, y queda un giro de -45 a 45 que si vale.
 * Mismo resultado en pantalla. El formato de 1.21.11 con los tres ejes (x, y, z) tampoco lo entienden:
 * si equivale a un giro solo en y, se reescribe como angle/axis.
 */
final class ModelRotations {

    /** Un cuarto de vuelta como el de un blockstate con y: 90 (la cara norte pasa a mirar al este). */
    private static final Map<String, String> QUARTER = Map.of("north", "east", "east", "south", "south", "west", "west", "north");

    private ModelRotations() {
    }

    /** El modelo con los giros convertidos, o el mismo si no hacia falta. */
    static byte[] convert(final byte[] model) {
        final JsonObject root;
        try {
            root = JsonParser.parseString(new String(model, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (final RuntimeException e) {
            return model;
        }
        if (!root.has("elements")) {
            return model;
        }
        boolean changed = false;
        for (final JsonElement element : root.getAsJsonArray("elements")) {
            changed |= convertElement(element.getAsJsonObject());
        }
        return changed ? root.toString().getBytes(StandardCharsets.UTF_8) : model;
    }

    private static boolean convertElement(final JsonObject element) {
        if (!element.has("rotation")) {
            return false;
        }
        final JsonObject rotation = element.getAsJsonObject("rotation");
        boolean changed = false;
        if (!rotation.has("angle")) {
            final Float y = eulerAsY(rotation);
            if (y == null) {
                return false;
            }
            rotation.remove("x");
            rotation.remove("y");
            rotation.remove("z");
            rotation.addProperty("angle", y);
            rotation.addProperty("axis", "y");
            changed = true;
        }
        final float angle = rotation.get("angle").getAsFloat();
        // shortcut: solo eje y, el unico que 26.3 usa fuera de rango; si aparece otro, el modelo falla como antes
        if (Math.abs(angle) <= 45.0F || !"y".equals(rotation.get("axis").getAsString())) {
            return changed;
        }
        final int turns = Math.round(angle / 90.0F);
        rotation.addProperty("angle", angle - turns * 90.0F);
        final JsonArray origin = rotation.getAsJsonArray("origin");
        // Un giro de -90 es un cuarto de vuelta; uno de +90, tres
        for (int i = 0; i < Math.floorMod(-turns, 4); i++) {
            quarterTurn(element, origin.get(0).getAsFloat(), origin.get(2).getAsFloat());
        }
        return true;
    }

    /**
     * El giro de 1.21.11 con tres ejes (Matrix4f.rotationZYX: primero x, luego y, luego z) como un solo
     * giro en y, si lo es; si no, null. Ej.: x 180, y -67.5, z -180 es y -112.5.
     */
    private static Float eulerAsY(final JsonObject rotation) {
        final double x = Math.toRadians(value(rotation, "x"));
        final double y = Math.toRadians(value(rotation, "y"));
        final double z = Math.toRadians(value(rotation, "z"));
        final org.joml.Matrix3d m = new org.joml.Matrix3d().rotationZYX(z, y, x);
        // Un giro solo en y deja el eje y quieto
        if (Math.abs(m.m11 - 1.0) > 1.0E-4) {
            return null;
        }
        // rotationY(b): x' = cos(b) x + sin(b) z (m00 = cos, m20 = sin en JOML)
        return (float) Math.toDegrees(Math.atan2(m.m20, m.m00));
    }

    private static double value(final JsonObject rotation, final String axis) {
        return rotation.has(axis) ? rotation.get(axis).getAsDouble() : 0.0;
    }

    /** Gira el cubo 90 grados alrededor del eje y que pasa por (ox, oz): (x, z) -> (ox - (z - oz), oz + (x - ox)). */
    private static void quarterTurn(final JsonObject element, final float ox, final float oz) {
        final JsonArray from = element.getAsJsonArray("from");
        final JsonArray to = element.getAsJsonArray("to");
        final float x1 = ox - (from.get(2).getAsFloat() - oz);
        final float x2 = ox - (to.get(2).getAsFloat() - oz);
        final float z1 = oz + (from.get(0).getAsFloat() - ox);
        final float z2 = oz + (to.get(0).getAsFloat() - ox);
        from.set(0, new com.google.gson.JsonPrimitive(Math.min(x1, x2)));
        to.set(0, new com.google.gson.JsonPrimitive(Math.max(x1, x2)));
        from.set(2, new com.google.gson.JsonPrimitive(Math.min(z1, z2)));
        to.set(2, new com.google.gson.JsonPrimitive(Math.max(z1, z2)));
        if (!element.has("faces")) {
            return;
        }
        final JsonObject faces = element.getAsJsonObject("faces");
        final JsonObject turned = new JsonObject();
        for (final Map.Entry<String, JsonElement> entry : faces.entrySet()) {
            final JsonObject face = entry.getValue().getAsJsonObject();
            if (face.has("cullface")) {
                final String cull = face.get("cullface").getAsString();
                face.addProperty("cullface", QUARTER.getOrDefault(cull, cull));
            }
            // Las caras de arriba y abajo giran sobre si mismas: su textura tambien
            final int textureTurn = switch (entry.getKey()) {
                case "up" -> 90;
                case "down" -> 270;
                default -> 0;
            };
            if (textureTurn != 0) {
                final int current = face.has("rotation") ? face.get("rotation").getAsInt() : 0;
                face.addProperty("rotation", (current + textureTurn) % 360);
            }
            turned.add(QUARTER.getOrDefault(entry.getKey(), entry.getKey()), face);
        }
        element.add("faces", turned);
    }
}
