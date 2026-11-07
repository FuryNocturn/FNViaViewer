package es.furynocturn.viaviewer.entity;

import com.viaversion.viabackwards.protocol.v1_21_11to1_21_9.Protocol1_21_11To1_21_9;
import com.viaversion.viabackwards.protocol.v1_21_9to1_21_7.Protocol1_21_9To1_21_7;
import com.viaversion.viabackwards.protocol.v26_2to26_1.Protocol26_2To26_1;
import com.viaversion.viabackwards.protocol.v26_3to26_2.Protocol26_3To26_2;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_21_11;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_21_9;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes26_2;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes26_3;
import com.viaversion.viaversion.api.minecraft.entitydata.EntityDataType;
import com.viaversion.viaversion.api.minecraft.entitydata.types.EntityDataTypes26_3;
import com.viaversion.viaversion.api.type.types.version.VersionedTypes;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.rewriter.entitydata.EntityDataFilter;
import es.furynocturn.viaviewer.config.ViewerConfig;
import es.furynocturn.viaviewer.via.ViaTranslator;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.Nullable;

/**
 * Entidades de 26.3 que ViaBackwards cambia por otras (barco de alamo -> de abedul, cojin -> bloque
 * que cae, sulfur cube -> slime). La red y la fisica siguen siendo las del sustituto; solo cambia como se dibujan. El tipo
 * real sale del seguimiento de entidades de Via en el paso 26.3 -> 26.2.
 */
public final class NewEntities {

    /** Y mobs de 1.21.9-1.21.11 que el cliente aun no tiene (nautilus -> calamar, golem de cobre -> rana...). */
    public enum Kind { POPLAR_BOAT, POPLAR_CHEST_BOAT, CUSHION, SULFUR_CUBE, NAUTILUS, ZOMBIE_NAUTILUS, CAMEL_HUSK, PARCHED, COPPER_GOLEM }

    private static final Map<EntityType, Kind> KINDS = Map.of(
        EntityTypes26_3.POPLAR_BOAT, Kind.POPLAR_BOAT, EntityTypes26_3.POPLAR_CHEST_BOAT, Kind.POPLAR_CHEST_BOAT,
        EntityTypes26_3.CUSHION, Kind.CUSHION, EntityTypes26_3.SULFUR_CUBE, Kind.SULFUR_CUBE,
        EntityTypes26_3.NAUTILUS, Kind.NAUTILUS, EntityTypes26_3.ZOMBIE_NAUTILUS, Kind.ZOMBIE_NAUTILUS,
        EntityTypes26_3.CAMEL_HUSK, Kind.CAMEL_HUSK, EntityTypes26_3.PARCHED, Kind.PARCHED, EntityTypes26_3.COPPER_GOLEM, Kind.COPPER_GOLEM);

    private static volatile @Nullable UserConnection connection;
    private static @Nullable Protocol26_3To26_2 protocol;
    private static final Map<Integer, DyeColor> CUSHION_COLORS = new ConcurrentHashMap<>();
    private static final Map<Integer, Boolean> CUBE_BABIES = new ConcurrentHashMap<>();
    private static final Map<Integer, Fuse> CUBE_FUSES = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> GOLEM_WEATHERING = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> NAUTILUS_VARIANTS = new ConcurrentHashMap<>();

    /** Mecha de un sulfur cube: la manda el servidor al encenderse y el cliente la descuenta cada tick. */
    static final class Fuse {
        final int max;
        int startTick = -1;

        Fuse(final int max) {
            this.max = max;
        }
    }

    private NewEntities() {
    }

    public static void onConnect(final UserConnection user) {
        connection = user;
        CUSHION_COLORS.clear();
        CUBE_BABIES.clear();
        CUBE_FUSES.clear();
        GOLEM_WEATHERING.clear();
        NAUTILUS_VARIANTS.clear();
    }

    /** Antes que los filtros de ViaBackwards que los borran, se apuntan el color del cojin y la cria y la mecha del sulfur cube. */
    public static void captureLostData() throws ReflectiveOperationException {
        protocol = Via.getManager().getProtocolManager().getProtocol(Protocol26_3To26_2.class);
        if (protocol == null || !(protocol.getEntityRewriter() instanceof EntityRewriter<?, ?> rewriter)) {
            ViaTranslator.LOGGER.warn("Sin el paso 26.3 -> 26.2 de ViaBackwards: los cojines saldran blancos");
            return;
        }
        // El color es el unico dato de tipo "color de tinte" del cojin; ViaBackwards quita ese tipo entero
        final EntityDataType dyeColor = ((EntityDataTypes26_3) VersionedTypes.V26_3.entityDataTypes).dyeColorType;
        final EntityDataFilter filter = rewriter.filter().type(EntityTypes26_3.CUSHION).dataType(dyeColor)
            .handlerNoRegister((event, data) -> {
                if (event.user() == connection && data.getValue() instanceof Integer id) {
                    CUSHION_COLORS.put(event.entityId(), DyeColor.byId(id));
                }
            }).build();
        addFirst(rewriter, filter);

        captureOlderMobs();

        final Protocol26_2To26_1 older = Via.getManager().getProtocolManager().getProtocol(Protocol26_2To26_1.class);
        if (older == null || !(older.getEntityRewriter() instanceof EntityRewriter<?, ?> olderRewriter)) {
            ViaTranslator.LOGGER.warn("Sin el paso 26.2 -> 26.1 de ViaBackwards: los sulfur cubes saldran siempre grandes y sin mecha");
            return;
        }
        // Indices de 26.2: 16 cria (AgeableMob), 20 mecha (-1 = apagada)
        addFirst(olderRewriter, olderRewriter.filter().type(EntityTypes26_2.SULFUR_CUBE).index(16).handlerNoRegister((event, data) -> {
            if (event.user() == connection && data.getValue() instanceof Boolean baby) {
                CUBE_BABIES.put(event.entityId(), baby);
            }
        }).build());
        addFirst(olderRewriter, olderRewriter.filter().type(EntityTypes26_2.SULFUR_CUBE).index(20).handlerNoRegister((event, data) -> {
            if (event.user() == connection && data.getValue() instanceof Integer fuse) {
                if (fuse >= 0) {
                    CUBE_FUSES.put(event.entityId(), new Fuse(fuse));
                } else {
                    CUBE_FUSES.remove(event.entityId());
                }
            }
        }).build());
    }

    /**
     * Mobs de 1.21.9-1.21.11 (solo pasan por aqui con clientes que no los tienen): ViaBackwards quita la
     * oxidacion del golem de cobre (indice 16 de 1.21.9) y la variante del zombie nautilus (20 de 1.21.11).
     */
    private static void captureOlderMobs() throws ReflectiveOperationException {
        final Protocol1_21_11To1_21_9 nautilus = Via.getManager().getProtocolManager().getProtocol(Protocol1_21_11To1_21_9.class);
        if (nautilus != null && nautilus.getEntityRewriter() instanceof EntityRewriter<?, ?> rewriter) {
            addFirst(rewriter, rewriter.filter().type(EntityTypes1_21_11.ZOMBIE_NAUTILUS).index(20).handlerNoRegister((event, data) -> {
                if (event.user() == connection && data.getValue() instanceof Integer variant) {
                    NAUTILUS_VARIANTS.put(event.entityId(), variant);
                }
            }).build());
        }
        final Protocol1_21_9To1_21_7 golem = Via.getManager().getProtocolManager().getProtocol(Protocol1_21_9To1_21_7.class);
        if (golem != null && golem.getEntityRewriter() instanceof EntityRewriter<?, ?> rewriter) {
            addFirst(rewriter, rewriter.filter().type(EntityTypes1_21_9.COPPER_GOLEM).index(16).handlerNoRegister((event, data) -> {
                if (event.user() == connection && data.getValue() instanceof Integer weathering) {
                    GOLEM_WEATHERING.put(event.entityId(), weathering);
                }
            }).build());
        }
    }

    private static void addFirst(final EntityRewriter<?, ?> rewriter, final EntityDataFilter filter) throws ReflectiveOperationException {
        final Field field = EntityRewriter.class.getDeclaredField("entityDataFilters");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        final List<EntityDataFilter> filters = (List<EntityDataFilter>) field.get(rewriter);
        filters.addFirst(filter);
    }

    /** La conexion actual pasa por Via (servidor de una version mas nueva). */
    public static boolean translating() {
        final UserConnection user = connection;
        return user != null && user.getChannel() != null && user.getChannel().isOpen();
    }

    public static @Nullable Kind kind(final Entity entity) {
        final UserConnection user = connection;
        if (user == null || protocol == null || !ViewerConfig.get().entities || !user.getChannel().isOpen()) {
            return null;
        }
        final EntityType type;
        try {
            final EntityTracker tracker = user.getEntityTracker(protocol);
            type = tracker != null ? tracker.entityType(entity.getId()) : null;
        } catch (final RuntimeException e) {
            return null; // el hilo de red esta cambiando el mapa justo ahora: se dibuja el sustituto este frame
        }
        return type != null ? KINDS.get(type) : null;
    }

    /** ViaBackwards pone el nombre del tipo real ("Cushion", "Sulfur Cube") como nombre visible: no es un nombre del jugador. */
    public static boolean hasReplacementName(final Entity entity, final String type) {
        final Component name = entity.getCustomName();
        if (name == null) {
            return false;
        }
        final String shown = name.getString();
        final String key = "entity.minecraft." + type;
        return shown.equalsIgnoreCase(type.replace('_', ' ')) || shown.equals(Language.getInstance().getOrDefault(key, key));
    }

    static boolean isBabyCube(final Entity entity) {
        return CUBE_BABIES.getOrDefault(entity.getId(), false);
    }

    /** Ticks que le quedan a la mecha (como en 26.3, con el tick parcial), o -1 si no esta encendida. */
    static float cubeFuse(final Entity entity, final float partialTick) {
        final Fuse fuse = CUBE_FUSES.get(entity.getId());
        if (fuse == null) {
            return -1.0F;
        }
        if (fuse.startTick < 0) {
            fuse.startTick = entity.tickCount;
        }
        return Math.max(0, fuse.max - (entity.tickCount - fuse.startTick)) - partialTick + 1.0F;
    }

    /** Oxidacion del golem de cobre: 0 normal, 1 expuesto, 2 erosionado, 3 oxidado. */
    public static int golemWeathering(final Entity entity) {
        return GOLEM_WEATHERING.getOrDefault(entity.getId(), 0);
    }

    /**
     * Variante del zombie nautilus: true si es la de coral (warm).
     * shortcut: el ID sale del registro que manda el servidor en orden alfabetico (temperate 0, warm 1); si 26.x anade variantes, leer el registro
     */
    public static boolean warmZombieNautilus(final Entity entity) {
        return NAUTILUS_VARIANTS.getOrDefault(entity.getId(), 0) == 1;
    }

    static DyeColor cushionColor(final Entity entity) {
        return CUSHION_COLORS.getOrDefault(entity.getId(), DyeColor.WHITE);
    }
}
