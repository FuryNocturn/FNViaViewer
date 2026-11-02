package es.furynocturn.viaviewer.entity;

import com.viaversion.viabackwards.protocol.v26_3to26_2.Protocol26_3To26_2;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.Nullable;

/**
 * Entidades de 26.3 que ViaBackwards cambia por otras (barco de alamo -> de abedul, cojin -> bloque
 * que cae). La red y la fisica siguen siendo las del sustituto; solo cambia como se dibujan. El tipo
 * real sale del seguimiento de entidades de Via en el paso 26.3 -> 26.2.
 */
public final class NewEntities {

    public enum Kind { POPLAR_BOAT, POPLAR_CHEST_BOAT, CUSHION }

    private static volatile @Nullable UserConnection connection;
    private static @Nullable Protocol26_3To26_2 protocol;
    private static final Map<Integer, DyeColor> CUSHION_COLORS = new ConcurrentHashMap<>();

    private NewEntities() {
    }

    public static void onConnect(final UserConnection user) {
        connection = user;
        CUSHION_COLORS.clear();
    }

    /** Antes que el filtro de ViaBackwards que lo borra, se apunta el color de cada cojin. */
    public static void captureCushionColors() throws ReflectiveOperationException {
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
        final Field field = EntityRewriter.class.getDeclaredField("entityDataFilters");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        final List<EntityDataFilter> filters = (List<EntityDataFilter>) field.get(rewriter);
        filters.addFirst(filter);
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
        return type == EntityTypes26_3.CUSHION ? Kind.CUSHION : null;
    }

    /** ViaBackwards pone "Cushion" como nombre visible al bloque que cae: no es un nombre del jugador. */
    static boolean hasReplacementName(final Entity entity) {
        return entity.getCustomName() != null && entity.getCustomName().getString().equalsIgnoreCase("cushion");
    }

    static DyeColor cushionColor(final Entity entity) {
        return CUSHION_COLORS.getOrDefault(entity.getId(), DyeColor.WHITE);
    }
}
