package es.furynocturn.viaviewer.content;

import com.viaversion.viabackwards.api.data.BackwardsMappingData;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.FullMappings;
import com.viaversion.viaversion.api.data.FullMappingsBase;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.util.Key;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * Cambia las tablas de bloques, estados, items, sonidos, particulas y entidades de bloque de cada paso de ViaBackwards de un
 * descriptor por {@link ExtendedMappings}. Sin clases de Minecraft: lo usa tambien FallbackTool.
 */
public final class MappingPatcher {

    /** IDs reales en el cliente de cada indice D y tamano del registro del cliente. */
    public record ClientIds(int[] ids, int registrySize) {
    }

    /** Lo que hay en el cliente para cada tipo de tabla. */
    public record Client(ClientIds blockStates, ClientIds blocks, ClientIds items, ClientIds sounds, ClientIds particles,
                         ClientIds blockEntities) {
    }

    /** Que tipos de contenido nuevo pasan (se consulta en cada paquete, asi se puede cambiar en marcha). */
    public record Toggles(BooleanSupplier blocks, BooleanSupplier items, BooleanSupplier sounds, BooleanSupplier particles) {
        public static final Toggles ALL = new Toggles(() -> true, () -> true, () -> true, () -> true);
    }

    private MappingPatcher() {
    }

    public static ProtocolVersion version(final String name) {
        for (final ProtocolVersion version : ProtocolVersion.getProtocols()) {
            if (version.getIncludedVersions().contains(name)) {
                return version;
            }
        }
        throw new IllegalArgumentException("Via no conoce la version " + name);
    }

    /** El protocolo de ViaBackwards de un paso (servidor 'from', cliente 'to'). */
    public static Protocol<?, ?, ?, ?> protocol(final ContentDescriptor.Step step) {
        final Protocol<?, ?, ?, ?> protocol = Via.getManager().getProtocolManager().getProtocol(version(step.to()), version(step.from()));
        if (protocol == null) {
            throw new IllegalStateException("No hay protocolo " + step.from() + " -> " + step.to());
        }
        return protocol;
    }

    public static void patch(final ContentDescriptor descriptor, final Client client, final Toggles toggles) throws ReflectiveOperationException {
        final List<ContentDescriptor.Step> steps = descriptor.steps();
        final List<String> blockNames = descriptor.blocks().stream().map(ContentDescriptor.BlockDef::id).toList();
        final List<String> itemNames = descriptor.items().stream().map(ContentDescriptor.Entry::id).toList();
        final List<String> soundNames = descriptor.sounds().stream().map(ContentDescriptor.Entry::id).toList();
        final List<String> particleNames = descriptor.particles().stream().map(ContentDescriptor.Entry::id).toList();
        final List<String> blockEntityNames = descriptor.blockEntities().stream().map(ContentDescriptor.Entry::id).toList();

        for (int i = 0; i < steps.size(); i++) {
            final ContentDescriptor.Step step = steps.get(i);
            final boolean first = i == 0;
            final boolean last = i == steps.size() - 1;
            final MappingData data = protocol(step).getMappingData();
            if (!(data instanceof MappingDataBase)) {
                throw new IllegalStateException("Paso " + step.from() + " -> " + step.to() + " sin tablas de Via");
            }
            replace(data, "blockStateMappings", step.blockStates(), first, descriptor.stateCount(), last ? client.blockStates() : null, null, toggles.blocks());
            replace(data, "blockMappings", step.blocks(), first, blockNames.size(), last ? client.blocks() : null, blockNames, toggles.blocks());
            replace(data, "itemMappings", step.items(), first, itemNames.size(), last ? client.items() : null, itemNames, toggles.items());
            replace(data, "soundMappings", step.sounds(), first, soundNames.size(), last ? client.sounds() : null, soundNames, toggles.sounds());
            // Las entidades de bloque nuevas van con su bloque: si el bloque vuelve al sustituto, la suya tambien
            replace(data, "blockEntityMappings", step.blockEntities(), first, blockEntityNames.size(), last ? client.blockEntities() : null,
                blockEntityNames, toggles.blocks());
            replace(data, "particleMappings", step.particles(), first, particleNames.size(), last ? client.particles() : null,
                particleNames, toggles.particles());

            // Sin esto ViaBackwards renombra los items nuevos ("26.3 Cinnabar") y les pone custom_model_data
            if (data instanceof BackwardsMappingData backwards && backwards.getBackwardsItemMappings() != null) {
                step.items().keySet().forEach(backwards.getBackwardsItemMappings()::remove);
            }
        }
    }

    private static void replace(final MappingData data, final String fieldName, final Int2IntMap introduced, final boolean first,
                                final int count, final ClientIds client, final List<String> names,
                                final BooleanSupplier active) throws ReflectiveOperationException {
        final Field field = MappingDataBase.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        final Mappings original = (Mappings) field.get(data);
        if (original == null) {
            if (!introduced.isEmpty() || (client != null && count > 0)) {
                // En el ultimo paso hacen falta los IDs reales del cliente: sin tabla no hay donde ponerlos
                throw new IllegalStateException(fieldName + " sin tabla en Via pero con contenido nuevo");
            }
            return; // paso sin cambios en esta tabla: Via deja los IDs tal cual y la zona extendida pasa igual
        }
        if (original instanceof ExtendedMappings || original instanceof ExtendedFull) {
            throw new IllegalStateException(fieldName + " ya parcheado");
        }
        final ExtendedMappings extended = new ExtendedMappings(original, introduced, first, count,
            client != null ? client.ids() : null, client != null ? client.registrySize() : -1, active);
        if (original instanceof ParticleMappings particles) {
            field.set(data, extendedParticles(particles, extended, names));
        } else {
            field.set(data, original instanceof FullMappings full && names != null ? extendedFull(full, extended, names) : extended);
        }
    }

    /**
     * ParticleMappings ademas guarda que IDs llevan bloque o item (lo calcula por nombre al construirse):
     * se rehace con las listas de nombres ampliadas. Huecos (IDs del cliente de otros mods) con nombre propio.
     */
    private static ParticleMappings extendedParticles(final ParticleMappings original, final ExtendedMappings extended, final List<String> names) {
        final FullMappings full = extendedFull(original, extended, names);
        return new ParticleMappings(new MappingDataLoader.IdentifiersPair(
            names(full::keyFromId, full.size()), names(full::mappedKeyFromMappedId, full.mappedSize())), extended);
    }

    private static List<String> names(final Function<Integer, Key> lookup, final int size) {
        final List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final Key key = lookup.apply(i);
            list.add(key != null ? Key.stripMinecraftNamespace(key.toString()) : "fnviaviewer:unknown_" + i);
        }
        return list;
    }

    /** Misma logica de nombres de Via (FullMappingsBase) con las listas ampliadas. */
    private static FullMappings extendedFull(final FullMappings original, final ExtendedMappings extended, final List<String> names) {
        final Key[] idToKey = keys(original.size(), extended.size(), original::keyFromId);
        final Key[] mappedIdToKey = keys(original.mappedSize(), extended.mappedSize(), original::mappedKeyFromMappedId);
        for (int d = 0; d < names.size(); d++) {
            final int source = extended.source(d);
            if (source >= original.size()) {
                idToKey[source] = Key.of(names.get(d));
            }
            // Solo los que llegan a este paso ya como nuevos tienen ID de salida extendido
            if (source != -1) {
                mappedIdToKey[extended.out(d)] = Key.of(names.get(d));
            }
        }
        return new ExtendedFull(index(idToKey), index(mappedIdToKey), idToKey, mappedIdToKey, extended);
    }

    private static Key[] keys(final int originalSize, final int size, final Function<Integer, Key> lookup) {
        final Key[] keys = new Key[size];
        for (int i = 0; i < originalSize; i++) {
            keys[i] = lookup.apply(i);
        }
        return keys;
    }

    private static Object2IntMap<String> index(final Key[] keys) {
        final Object2IntMap<String> map = new Object2IntOpenHashMap<>(keys.length);
        map.defaultReturnValue(-1);
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) {
                map.put(Key.stripMinecraftNamespace(keys[i].toString()), i);
            }
        }
        return map;
    }

    static final class ExtendedFull extends FullMappingsBase {
        ExtendedFull(final Object2IntMap<String> stringToId, final Object2IntMap<String> mappedStringToId,
                     final Key[] idToKey, final Key[] mappedIdToKey, final Mappings mappings) {
            super(stringToId, mappedStringToId, idToKey, mappedIdToKey, mappings);
        }
    }

    /** Para FallbackTool: los IDs de salida del ultimo paso tal como los daria un cliente vanilla + contenido. */
    public static ClientIds sequential(final int nativeSize, final int count) {
        final int[] ids = new int[count];
        for (int d = 0; d < count; d++) {
            ids[d] = nativeSize + d;
        }
        return new ClientIds(ids, nativeSize + count);
    }
}
