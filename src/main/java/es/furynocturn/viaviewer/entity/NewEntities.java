package es.furynocturn.viaviewer.entity;

import com.viaversion.viabackwards.protocol.v26_2to26_1.Protocol26_2To26_1;
import com.viaversion.viabackwards.protocol.v26_3to26_2.Protocol26_3To26_2;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
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

    public enum Kind { POPLAR_BOAT, POPLAR_CHEST_BOAT, CUSHION, SULFUR_CUBE }

    private static volatile @Nullable UserConnection connection;
    private static @Nullable Protocol26_3To26_2 protocol;
    private static final Map<Integer, DyeColor> CUSHION_COLORS = new ConcurrentHashMap<>();
    private static final Map<Integer, Boolean> CUBE_BABIES = new ConcurrentHashMap<>();
    private static final Map<Integer, Fuse> CUBE_FUSES = new ConcurrentHashMap<>();

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
        if (type == EntityTypes26_3.POPLAR_BOAT) {
            return Kind.POPLAR_BOAT;
        }
        if (type == EntityTypes26_3.POPLAR_CHEST_BOAT) {
            return Kind.POPLAR_CHEST_BOAT;
        }
        if (type == EntityTypes26_3.SULFUR_CUBE) {
            return Kind.SULFUR_CUBE;
        }
        return type == EntityTypes26_3.CUSHION ? Kind.CUSHION : null;
    }

    /** ViaBackwards pone el nombre del tipo real ("Cushion", "Sulfur Cube") como nombre visible: no es un nombre del jugador. */
    static boolean hasReplacementName(final Entity entity, final String type) {
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

    static DyeColor cushionColor(final Entity entity) {
        return CUSHION_COLORS.getOrDefault(entity.getId(), DyeColor.WHITE);
    }
}
