package es.furynocturn.viaviewer.content;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

/**
 * Azufre potente (geiser) de 26.2+. El servidor decide el estado (seco, mojado, dormido, en erupcion,
 * continuo) y avisa de cada erupcion con un evento de bloque; burbujas, gas, chorro y empuje hacia
 * arriba los calcula el cliente, como en la version nueva. Reprogramado a partir de su comportamiento.
 */
public final class PotentSulfurBlock extends BaseEntityBlock {

    private static final ThreadLocal<Property<?>> BUILDING = new ThreadLocal<>();
    private static final MapCodec<PotentSulfurBlock> CODEC = simpleCodec(p -> {
        throw new UnsupportedOperationException("Bloque solo de cliente");
    });

    private final Property<?> stateProperty;

    public static PotentSulfurBlock create(final Property<?> stateProperty, final Properties properties) {
        BUILDING.set(stateProperty);
        try {
            return new PotentSulfurBlock(stateProperty, properties);
        } finally {
            BUILDING.remove();
        }
    }

    private PotentSulfurBlock(final Property<?> stateProperty, final Properties properties) {
        super(properties);
        this.stateProperty = stateProperty;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BUILDING.get());
    }

    /** dry, wet, dormant, erupting o continuous. */
    String stateName(final BlockState state) {
        return name(state, this.stateProperty);
    }

    private static <T extends Comparable<T>> String name(final BlockState state, final Property<T> property) {
        return property.getName(state.getValue(property));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new PotentSulfurBlockEntity(pos, state);
    }

    @Override
    public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
        if (this.stateName(state).equals("dry") || !level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER)) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            level.addAlwaysVisibleParticle(NewParticles.simple("minecraft:sulfur_bubbles"),
                pos.getX() + random.nextFloat(), pos.getY() + 1 + random.nextFloat(), pos.getZ() + random.nextFloat(), 0, 0, 0);
        }
        if (random.nextInt(10) == 0) {
            level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), sound("block.potent_sulfur.noxious_gas"), SoundSource.AMBIENT, 1.0F, 1.0F, false);
        }
    }

    @Override
    protected boolean triggerEvent(final BlockState state, final Level level, final BlockPos pos, final int id, final int param) {
        // El servidor manda el evento 0 al empezar una erupcion: el chorro y su sonido se cuentan desde aqui
        if (level.getBlockEntity(pos) instanceof PotentSulfurBlockEntity entity) {
            entity.eruptionTick = level.getGameTime();
        }
        return true;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState state, final BlockEntityType<T> type) {
        if (!level.isClientSide() || type != PotentSulfurBlockEntity.type()) {
            return null;
        }
        final BlockEntityTicker<PotentSulfurBlockEntity> ticker = switch (this.stateName(state)) {
            case "wet", "dormant" -> PotentSulfurBlockEntity::tickGas;
            case "erupting" -> (l, p, s, e) -> e.tickEruption(l, p, sound("block.potent_sulfur.geyser_eruption_active"));
            case "continuous" -> (l, p, s, e) -> e.tickEruption(l, p, sound("block.potent_sulfur.geyser_continuous_eruption_active"));
            default -> null;
        };
        @SuppressWarnings("unchecked")
        final BlockEntityTicker<T> cast = (BlockEntityTicker<T>) ticker;
        return cast;
    }

    static SoundEvent sound(final String path) {
        return BuiltInRegistries.SOUND_EVENT.getValue(Identifier.withDefaultNamespace(path));
    }
}
