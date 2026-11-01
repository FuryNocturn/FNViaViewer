package es.furynocturn.viaviewer.content;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Bloque de una version nueva registrado solo para verlo: el modelo y la textura son los suyos
 * (resource pack de la cache) y todo lo que afecta a fisica o luz se copia, estado a estado, del
 * sustituto que pondria ViaBackwards. Asi la prediccion del cliente coincide con el servidor.
 */
public final class NewBlock extends Block {

    /** Mientras se construye el bloque aun no tiene campos: createBlockStateDefinition lo lee de aqui. */
    private static final ThreadLocal<Layout> BUILDING = new ThreadLocal<>();

    private final Layout layout;
    private final Map<BlockState, BlockState> fallbackByState = new IdentityHashMap<>();

    /**
     * Propiedades del bloque y sustituto de cada estado. El indice de un estado se calcula de sus
     * valores igual que StateDefinition (propiedades por nombre, la ultima cambia mas rapido).
     */
    public record Layout(List<Property<?>> properties, Map<String, List<String>> values, BlockState[] fallbacks) {

        public Layout {
            properties = properties.stream().sorted(Comparator.comparing(Property::getName)).toList();
        }

        public int index(final StateHolder<?, ?> state) {
            int index = 0;
            for (final Property<?> property : this.properties) {
                final List<String> names = this.values.get(property.getName());
                index = index * names.size() + names.indexOf(valueName(state, property));
            }
            return index;
        }

        public BlockState fallback(final StateHolder<?, ?> state) {
            return this.fallbacks[this.index(state)];
        }

        private static <T extends Comparable<T>> String valueName(final StateHolder<?, ?> state, final Property<T> property) {
            return property.getName(state.getValue(property));
        }
    }

    public static NewBlock create(final Layout layout, final Properties properties, final Map<String, String> defaults) {
        BUILDING.set(layout);
        try {
            return new NewBlock(layout, properties, defaults);
        } finally {
            BUILDING.remove();
        }
    }

    private NewBlock(final Layout layout, final Properties properties, final Map<String, String> defaults) {
        super(properties);
        this.layout = layout;
        this.stateDefinition.getPossibleStates().forEach(state -> this.fallbackByState.put(state, layout.fallback(state)));
        BlockState state = this.stateDefinition.any();
        for (final Property<?> property : layout.properties()) {
            if (defaults.containsKey(property.getName())) {
                state = withValue(state, property, defaults.get(property.getName()));
            }
        }
        this.registerDefaultState(state);
    }

    public Layout layout() {
        return this.layout;
    }

    private static <T extends Comparable<T>> BlockState withValue(final BlockState state, final Property<T> property, final String name) {
        return property.getValue(name).map(value -> state.setValue(property, value)).orElse(state);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BUILDING.get().properties().toArray(Property[]::new));
    }

    private BlockState fb(final BlockState state) {
        // BlockStateBase llama a algunos de estos metodos mientras se construye el bloque (sin campos aun)
        if (this.fallbackByState == null) {
            return BUILDING.get().fallback(state);
        }
        return this.fallbackByState.get(state);
    }

    @Override
    protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return this.fb(state).getShape(level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return this.fb(state).getCollisionShape(level, pos, context);
    }

    @Override
    protected VoxelShape getVisualShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return this.fb(state).getVisualShape(level, pos, context);
    }

    @Override
    protected VoxelShape getInteractionShape(final BlockState state, final BlockGetter level, final BlockPos pos) {
        return this.fb(state).getInteractionShape(level, pos);
    }

    @Override
    protected VoxelShape getBlockSupportShape(final BlockState state, final BlockGetter level, final BlockPos pos) {
        return this.fb(state).getBlockSupportShape(level, pos);
    }

    @Override
    protected VoxelShape getOcclusionShape(final BlockState state) {
        return this.fb(state).getOcclusionShape();
    }

    @Override
    protected boolean useShapeForLightOcclusion(final BlockState state) {
        return this.fb(state).useShapeForLightOcclusion();
    }

    @Override
    protected boolean propagatesSkylightDown(final BlockState state) {
        return this.fb(state).propagatesSkylightDown();
    }

    @Override
    protected int getLightBlock(final BlockState state) {
        return this.fb(state).getLightBlock();
    }

    @Override
    protected float getShadeBrightness(final BlockState state, final BlockGetter level, final BlockPos pos) {
        return this.fb(state).getShadeBrightness(level, pos);
    }

    @Override
    protected FluidState getFluidState(final BlockState state) {
        return this.fb(state).getFluidState();
    }

    @Override
    protected boolean skipRendering(final BlockState state, final BlockState adjacent, final Direction direction) {
        // Caras entre dos bloques iguales (cristal, hojas...): como el sustituto consigo mismo
        if (adjacent.is(this)) {
            final BlockState fallback = this.fb(state);
            return fallback.skipRendering(this.fb(adjacent), direction);
        }
        return super.skipRendering(state, adjacent, direction);
    }

    @Override
    protected RenderShape getRenderShape(final BlockState state) {
        return RenderShape.MODEL;
    }
}
