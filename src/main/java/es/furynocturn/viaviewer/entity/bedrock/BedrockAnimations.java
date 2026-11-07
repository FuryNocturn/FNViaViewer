package es.furynocturn.viaviewer.entity.bedrock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import org.joml.Vector3f;

/**
 * Animaciones de Bedrock (*.animation.json) como AnimationDefinition de Java, para que las mueva el
 * mismo sistema de fotogramas que usa vanilla. Los valores son los mismos en las dos ediciones (el
 * golem de cobre de 26.3 los tiene identicos en Java). Solo se leen los numeros: los canales con
 * expresiones Molang (mirar al objetivo...) los hace el modelo con codigo, como Java.
 */
final class BedrockAnimations {

    private BedrockAnimations() {
    }

    static Map<String, AnimationDefinition> load(final Path file) throws IOException {
        final JsonObject animations = JsonParser.parseString(Files.readString(file)).getAsJsonObject().getAsJsonObject("animations");
        final Map<String, AnimationDefinition> out = new HashMap<>();
        for (final Map.Entry<String, JsonElement> entry : animations.entrySet()) {
            final AnimationDefinition definition = definition(entry.getValue().getAsJsonObject());
            if (definition != null) {
                out.put(entry.getKey(), definition);
            }
        }
        return out;
    }

    private static AnimationDefinition definition(final JsonObject animation) {
        if (!animation.has("bones")) {
            return null;
        }
        final float length = animation.has("animation_length") ? animation.get("animation_length").getAsFloat() : 0.0F;
        AnimationDefinition.Builder builder = AnimationDefinition.Builder.withLength(length);
        if (animation.has("loop") && animation.get("loop").isJsonPrimitive() && animation.get("loop").getAsJsonPrimitive().isBoolean()
            && animation.get("loop").getAsBoolean()) {
            builder = builder.looping();
        }
        boolean any = false;
        for (final Map.Entry<String, JsonElement> bone : animation.getAsJsonObject("bones").entrySet()) {
            final JsonObject channels = bone.getValue().getAsJsonObject();
            for (final String target : new String[]{"rotation", "position", "scale"}) {
                if (!channels.has(target)) {
                    continue;
                }
                final Keyframe[] keyframes = keyframes(channels.get(target), target);
                if (keyframes != null) {
                    builder.addAnimation(bone.getKey(), new AnimationChannel(switch (target) {
                        case "rotation" -> AnimationChannel.Targets.ROTATION;
                        case "position" -> AnimationChannel.Targets.POSITION;
                        default -> AnimationChannel.Targets.SCALE;
                    }, keyframes));
                    any = true;
                }
            }
        }
        return any ? builder.build() : null;
    }

    /** Los fotogramas de un canal, o null si tiene Molang. */
    private static Keyframe[] keyframes(final JsonElement channel, final String target) {
        final List<Keyframe> out = new ArrayList<>();
        if (channel.isJsonObject()) {
            for (final Map.Entry<String, JsonElement> frame : channel.getAsJsonObject().entrySet()) {
                JsonElement value = frame.getValue();
                AnimationChannel.Interpolation interpolation = AnimationChannel.Interpolations.LINEAR;
                if (value.isJsonObject()) {
                    final JsonObject complex = value.getAsJsonObject();
                    if (complex.has("lerp_mode") && "catmullrom".equals(complex.get("lerp_mode").getAsString())) {
                        interpolation = AnimationChannel.Interpolations.CATMULLROM;
                    }
                    value = complex.has("post") ? complex.get("post") : complex.get("pre");
                }
                final Vector3f vector = vector(value, target);
                if (vector == null) {
                    return null;
                }
                out.add(new Keyframe(Float.parseFloat(frame.getKey()), vector, interpolation));
            }
        } else {
            final Vector3f vector = vector(channel, target);
            if (vector == null) {
                return null;
            }
            out.add(new Keyframe(0.0F, vector, AnimationChannel.Interpolations.LINEAR));
        }
        out.sort((a, b) -> Float.compare(a.timestamp(), b.timestamp()));
        return out.isEmpty() ? null : out.toArray(Keyframe[]::new);
    }

    private static Vector3f vector(final JsonElement value, final String target) {
        if (value == null || !value.isJsonArray()) {
            return null;
        }
        final JsonArray array = value.getAsJsonArray();
        final float[] v = new float[3];
        for (int i = 0; i < 3; i++) {
            final JsonElement component = array.get(Math.min(i, array.size() - 1));
            if (!component.isJsonPrimitive() || !component.getAsJsonPrimitive().isNumber()) {
                return null;
            }
            v[i] = component.getAsFloat();
        }
        return switch (target) {
            case "rotation" -> KeyframeAnimations.degreeVec(v[0], v[1], v[2]);
            case "position" -> KeyframeAnimations.posVec(v[0], v[1], v[2]);
            default -> KeyframeAnimations.scaleVec(v[0], v[1], v[2]);
        };
    }
}
