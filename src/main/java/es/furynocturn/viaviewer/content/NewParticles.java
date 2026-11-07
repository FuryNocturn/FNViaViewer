package es.furynocturn.viaviewer.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * Tipos de particula de la version nueva, con el mismo formato de datos en la red que alli
 * (Via los pasa tal cual: solo cambia el ID).
 */
public final class NewParticles {

    /** geyser y geyser_plume: cuantos bloques de agua hay encima del azufre. */
    public record Geyser(ParticleType<Geyser> type, int waterBlocks) implements ParticleOptions {
        @Override
        public ParticleType<Geyser> getType() {
            return this.type;
        }
    }

    /** geyser_base y geyser_poof: ademas, la fuerza del chorro. */
    public record GeyserBase(ParticleType<GeyserBase> type, int waterBlocks, float burstImpulseBase) implements ParticleOptions {
        @Override
        public ParticleType<GeyserBase> getType() {
            return this.type;
        }
    }

    private static final Set<String> GEYSER = Set.of("minecraft:geyser", "minecraft:geyser_plume");
    private static final Set<String> GEYSER_BASE = Set.of("minecraft:geyser_base", "minecraft:geyser_poof");
    private static final Map<String, ParticleType<?>> BY_ID = new HashMap<>();
    private static final List<ParticleType<?>> TYPES = new ArrayList<>();

    private NewParticles() {
    }

    static void register(final List<ContentDescriptor.Entry> particles) {
        for (final ContentDescriptor.Entry entry : particles) {
            final ParticleType<?> type;
            if (GEYSER.contains(entry.id())) {
                type = FabricParticleTypes.<Geyser>complex(true, t -> RecordCodecBuilder.mapCodec(i -> i.group(
                        Codec.INT.fieldOf("water_blocks").forGetter(Geyser::waterBlocks)).apply(i, w -> new Geyser(t, w))),
                    t -> StreamCodec.composite(ByteBufCodecs.INT, Geyser::waterBlocks, w -> new Geyser(t, w)));
            } else if (GEYSER_BASE.contains(entry.id())) {
                type = FabricParticleTypes.<GeyserBase>complex(true, t -> RecordCodecBuilder.mapCodec(i -> i.group(
                        Codec.INT.fieldOf("water_blocks").forGetter(GeyserBase::waterBlocks),
                        Codec.FLOAT.fieldOf("burst_impulse_base").forGetter(GeyserBase::burstImpulseBase))
                        .apply(i, (w, b) -> new GeyserBase(t, w, b))),
                    t -> StreamCodec.composite(ByteBufCodecs.INT, GeyserBase::waterBlocks, ByteBufCodecs.FLOAT, GeyserBase::burstImpulseBase,
                        (w, b) -> new GeyserBase(t, w, b)));
            } else {
                type = FabricParticleTypes.simple(false);
            }
            TYPES.add(Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.parse(entry.id()), type));
            BY_ID.put(entry.id(), type);
        }
    }

    public static List<ParticleType<?>> types() {
        return TYPES;
    }

    private static ParticleType<?> get(final String id) {
        final ParticleType<?> type = BY_ID.get(id);
        if (type == null) {
            throw new IllegalStateException("Particula no registrada: " + id);
        }
        return type;
    }

    /** Si la particula es nueva para este cliente (las que ya trae de serie no estan en el descriptor). */
    public static boolean has(final String id) {
        return BY_ID.containsKey(id);
    }

    public static SimpleParticleType simple(final String id) {
        return (SimpleParticleType) get(id);
    }

    @SuppressWarnings("unchecked")
    public static ParticleType<Geyser> geyser(final String id) {
        return (ParticleType<Geyser>) get(id);
    }

    @SuppressWarnings("unchecked")
    public static ParticleType<GeyserBase> geyserBase(final String id) {
        return (ParticleType<GeyserBase>) get(id);
    }
}
