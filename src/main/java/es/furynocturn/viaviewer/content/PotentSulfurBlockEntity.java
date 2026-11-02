package es.furynocturn.viaviewer.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jspecify.annotations.Nullable;

/**
 * Lo que hace en el cliente la entidad de bloque del geiser: nube de gas cada segundo mientras esta
 * mojado o dormido, y durante la erupcion chorro cada segundo, sonido cada dos y empuje hacia arriba
 * de lo que controla este cliente (el jugador). Valores medidos en 26.3.
 */
public final class PotentSulfurBlockEntity extends BlockEntity {

    /** Hasta cuantos bloques de agua encima sigue funcionando el geiser. */
    private static final int MAX_WATER_BLOCKS = 4;
    private static final TagKey<EntityType<?>> NOT_AFFECTED = TagKey.create(Registries.ENTITY_TYPE,
        Identifier.withDefaultNamespace("not_affected_by_geysers"));

    private static BlockEntityType<PotentSulfurBlockEntity> type;

    long eruptionTick = -1;

    PotentSulfurBlockEntity(final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    static void setType(final BlockEntityType<PotentSulfurBlockEntity> value) {
        type = value;
    }

    public static BlockEntityType<PotentSulfurBlockEntity> type() {
        return type;
    }

    @Override
    public void setLevel(final Level level) {
        super.setLevel(level);
        if (this.eruptionTick == -1) {
            this.eruptionTick = level.getGameTime();
        }
    }

    static void tickGas(final Level level, final BlockPos pos, final BlockState state, final PotentSulfurBlockEntity entity) {
        if (level.getGameTime() % 20 == 0) {
            final BlockPos source = findGasSource(level, pos);
            if (source != null) {
                level.addParticle(NewParticles.simple("minecraft:noxious_gas_cloud"), source.getX() + 0.5, source.getY() + 0.5,
                    source.getZ() + 0.5, 0, 0, 0);
            }
        }
    }

    void tickEruption(final Level level, final BlockPos pos, final SoundEvent sound) {
        final BlockPos source = findGasSource(level, pos);
        if (source == null) {
            return;
        }
        final int waterBlocks = source.getY() - pos.getY() - 1;
        final long sinceEruption = level.getGameTime() - this.eruptionTick;
        if (sinceEruption % 20 == 0) {
            level.addParticle(new NewParticles.Geyser(NewParticles.geyser("minecraft:geyser"), waterBlocks),
                source.getX() + 0.5, source.getY(), source.getZ() + 0.5, 0, 0, 0);
        }
        if (sinceEruption % 40 == 0) {
            level.playLocalSound(source.getX() + 0.5, source.getY() + 0.5, source.getZ() + 0.5, sound, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }
        launch(level, pos, waterBlocks);
    }

    /** El chorro sube 6 bloques por cada bloque de agua, hasta el primer bloque solido. */
    private static void launch(final Level level, final BlockPos pos, final int waterBlocks) {
        final int height = clearHeight(level, pos.above(), 6 * waterBlocks);
        final AABB area = new AABB(pos.above()).expandTowards(0, height - 1, 0);
        for (final Entity entity : level.getEntitiesOfClass(Entity.class, area, EntitySelector.NO_SPECTATORS.and(EntitySelector.ENTITY_STILL_ALIVE))) {
            final Vec3 motion = entity.getDeltaMovement();
            entity.checkFallDistanceAccumulation();
            if (entity.canSimulateMovement() && !(entity instanceof Player player && player.getAbilities().flying) && !entity.isPassenger()
                && !entity.getType().is(NOT_AFFECTED) && motion.y < 0.3 + waterBlocks * 0.1) {
                entity.addDeltaMovement(new Vec3(0, 0.2, 0));
            }
        }
    }

    private static int clearHeight(final Level level, final BlockPos start, final int max) {
        final CollisionContext context = CollisionContext.withPosition(null, start.getY() - 1);
        for (int i = 0; i < max; i++) {
            final BlockPos at = start.above(i);
            if (!passable(level.getBlockState(at), level, at, context)) {
                return i;
            }
        }
        return max;
    }

    private static boolean passable(final BlockState state, final Level level, final BlockPos pos, final CollisionContext context) {
        return state.isAir() || state.is(Blocks.WATER) || state.getCollisionShape(level, pos, context).isEmpty();
    }

    /**
     * Primer bloque sin agua encima del azufre (por ahi sale el gas), si la columna de agua no pasa
     * de {@link #MAX_WATER_BLOCKS} y no la tapa un bloque solido.
     */
    static @Nullable BlockPos findGasSource(final Level level, final BlockPos origin) {
        final CollisionContext context = CollisionContext.withPosition(null, origin.getY());
        final BlockPos.MutableBlockPos pos = origin.above().mutable();
        while (pos.getY() <= origin.getY() + MAX_WATER_BLOCKS + 1) {
            final BlockState state = level.getBlockState(pos);
            final boolean water = level.getFluidState(pos).isSourceOfType(Fluids.WATER);
            if (!water || !state.is(Blocks.WATER) && !passable(state, level, pos, context)) {
                return state.isAir() || passable(state, level, pos, context) ? pos.immutable() : null;
            }
            pos.move(0, 1, 0);
        }
        return null;
    }

    /** Si el gas que sale de source llega a point: a menos de 3 bloques, sobre agua y sin paredes en medio. */
    public static boolean gasReaches(final Level level, final BlockPos source, final Vec3 point) {
        final BlockPos at = BlockPos.containing(point);
        if (!passable(level.getBlockState(at), level, at, CollisionContext.withPosition(null, at.getY() - 1))
            || point.distanceToSqr(Vec3.atCenterOf(source)) > 9.0) {
            return false;
        }
        final Vec3 below = new Vec3(point.x, point.y - 1, point.z);
        return level.getFluidState(BlockPos.containing(below)).isSourceOfType(Fluids.WATER)
            && level.clip(new ClipContext(Vec3.atCenterOf(source.below()), below, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
                CollisionContext.empty())).getType() != HitResult.Type.BLOCK;
    }
}
