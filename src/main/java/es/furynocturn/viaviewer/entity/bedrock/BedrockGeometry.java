package es.furynocturn.viaviewer.entity.bedrock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Geometria de Bedrock (*.geo.json de Mojang/bedrock-samples, bajada a la cache) como LayerDefinition
 * de Java. Comprobado contra los modelos Java de 26.3 del nautilus, el golem de cobre y los corales:
 * <ul>
 *   <li>un punto de Bedrock (x, y, z) es (x, 24 - y, z) en Java; cada parte va respecto a su padre;</li>
 *   <li>un cubo (origen o, tamano s) en una parte con pivote p es addBox(o.x - p.x, p.y - o.y - s.y, o.z - p.z, s);</li>
 *   <li>los giros tienen el mismo signo, en grados; un cubo girado pasa a una parte hija con su pivote.</li>
 * </ul>
 */
final class BedrockGeometry {

    private BedrockGeometry() {
    }

    /**
     * @param rename  nombre de Bedrock -> nombre de la parte en Java (los que no estan se quedan igual)
     * @param flatten todas las partes colgando de la raiz (como el HumanoidModel de Java), sin las que no estan en rename
     * @param needed  partes que pide el modelo de Java aunque Bedrock no las tenga (se anaden vacias; "a/b" es b dentro de a)
     */
    static LayerDefinition load(final Path file, final String geometry, final Map<String, String> rename, final boolean flatten,
                                final List<String> needed) throws IOException {
        final JsonObject root = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        JsonObject found = null;
        for (final JsonElement element : root.getAsJsonArray("minecraft:geometry")) {
            final JsonObject candidate = element.getAsJsonObject();
            if (candidate.getAsJsonObject("description").get("identifier").getAsString().equals(geometry)) {
                found = candidate;
            }
        }
        if (found == null) {
            throw new IOException(file.getFileName() + " sin " + geometry);
        }
        final JsonObject description = found.getAsJsonObject("description");
        final MeshDefinition mesh = new MeshDefinition();
        final Map<String, PartDefinition> parts = new HashMap<>();
        final Map<String, float[]> pivots = new HashMap<>();
        // Los huesos vienen con el padre antes que los hijos
        for (final JsonElement element : found.getAsJsonArray("bones")) {
            final JsonObject bone = element.getAsJsonObject();
            final String name = bone.get("name").getAsString();
            final float[] pivot = vec(bone, "pivot", 0.0F);
            pivots.put(name, pivot);
            if (flatten && !rename.containsKey(name)) {
                continue;
            }
            final String parentName = bone.has("parent") ? bone.get("parent").getAsString() : null;
            final PartDefinition parent = flatten || parentName == null ? mesh.getRoot() : parts.getOrDefault(parentName, mesh.getRoot());
            final float[] parentPivot = flatten || parentName == null ? null : pivots.get(parentName);
            final float[] rotation = vec(bone, "rotation", 0.0F);

            final CubeListBuilder cubes = CubeListBuilder.create();
            final java.util.List<JsonObject> rotated = new java.util.ArrayList<>();
            if (bone.has("cubes")) {
                for (final JsonElement cubeElement : bone.getAsJsonArray("cubes")) {
                    final JsonObject cube = cubeElement.getAsJsonObject();
                    if (!cube.has("uv") || !cube.get("uv").isJsonArray()) {
                        continue; // shortcut: UV por cara no lo tiene ningun mob que se use; si llega, ese cubo no sale
                    }
                    if (cube.has("rotation")) {
                        rotated.add(cube);
                    } else {
                        addBox(cubes, cube, pivot);
                    }
                }
            }
            final PartDefinition part = parent.addOrReplaceChild(rename.getOrDefault(name, name), cubes, pose(pivot, parentPivot, rotation));
            for (int i = 0; i < rotated.size(); i++) {
                final JsonObject cube = rotated.get(i);
                final float[] cubePivot = vec(cube, "pivot", 0.0F);
                final CubeListBuilder single = CubeListBuilder.create();
                addBox(single, cube, cubePivot);
                part.addOrReplaceChild(name + "_cube" + i, single, pose(cubePivot, pivot, vec(cube, "rotation", 0.0F)));
            }
            parts.put(name, part);
        }
        for (final String path : needed) {
            // "head/hat": la parte hat dentro de head
            PartDefinition parent = mesh.getRoot();
            final String[] names = path.split("/");
            for (int i = 0; i < names.length; i++) {
                final String name = names[i];
                final PartDefinition current = parent;
                if (current.getChildren().stream().noneMatch(e -> e.getKey().equals(name))) {
                    current.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.ZERO);
                }
                parent = current.getChild(name);
            }
        }
        return LayerDefinition.create(mesh, description.get("texture_width").getAsInt(), description.get("texture_height").getAsInt());
    }

    private static void addBox(final CubeListBuilder builder, final JsonObject cube, final float[] pivot) {
        final float[] origin = vec(cube, "origin", 0.0F);
        final float[] size = vec(cube, "size", 0.0F);
        final JsonArray uv = cube.getAsJsonArray("uv");
        final float inflate = cube.has("inflate") ? cube.get("inflate").getAsFloat() : 0.0F;
        builder.texOffs(uv.get(0).getAsInt(), uv.get(1).getAsInt())
            .mirror(cube.has("mirror") && cube.get("mirror").getAsBoolean())
            .addBox(origin[0] - pivot[0], pivot[1] - origin[1] - size[1], origin[2] - pivot[2], size[0], size[1], size[2],
                new CubeDeformation(inflate));
    }

    /** Posicion (respecto al pivote del padre, o a la raiz de Java si no tiene) y giro de una parte. */
    private static PartPose pose(final float[] pivot, final float[] parentPivot, final float[] rotation) {
        final float x = pivot[0] - (parentPivot != null ? parentPivot[0] : 0.0F);
        final float y = parentPivot != null ? parentPivot[1] - pivot[1] : 24.0F - pivot[1];
        final float z = pivot[2] - (parentPivot != null ? parentPivot[2] : 0.0F);
        return PartPose.offsetAndRotation(x, y, z,
            (float) Math.toRadians(rotation[0]), (float) Math.toRadians(rotation[1]), (float) Math.toRadians(rotation[2]));
    }

    private static float[] vec(final JsonObject object, final String key, final float fallback) {
        if (!object.has(key)) {
            return new float[]{fallback, fallback, fallback};
        }
        final JsonArray array = object.getAsJsonArray(key);
        return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()};
    }
}
