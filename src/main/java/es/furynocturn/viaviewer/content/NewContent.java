package es.furynocturn.viaviewer.content;

import es.furynocturn.viaviewer.mixin.BlockBehaviourAccessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Registra en el cliente los bloques, items y sonidos del descriptor, con su nombre de la version
 * nueva (minecraft:...) para que los assets y los shaderpacks los encuentren tal cual.
 * Van al final de los registros: los IDs vanilla no cambian.
 */
public final class NewContent implements ModInitializer {

    private static ContentDescriptor descriptor;
    private static final List<NewBlock> BLOCKS = new ArrayList<>();
    private static final List<Item> ITEMS = new ArrayList<>();
    private static final List<SoundEvent> SOUNDS = new ArrayList<>();

    @Override
    public void onInitialize() {
        try (InputStream in = NewContent.class.getResourceAsStream("/fnviaviewer/content/26.3.json")) {
            descriptor = ContentDescriptor.load(in);
        } catch (final IOException e) {
            throw new UncheckedIOException("No se pudo leer el descriptor de contenido", e);
        }
        if (descriptor.fallback() == null || descriptor.fallback().length != descriptor.stateCount()) {
            throw new IllegalStateException("Descriptor sin sustitutos: falta ejecutar gradlew fallbackTool");
        }

        for (final ContentDescriptor.Entry sound : descriptor.sounds()) {
            final Identifier id = Identifier.parse(sound.id());
            SOUNDS.add(Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id)));
        }

        final Map<String, Block> blocksById = new HashMap<>();
        for (final ContentDescriptor.BlockDef def : descriptor.blocks()) {
            final NewBlock block = Registry.register(BuiltInRegistries.BLOCK, blockKey(def.id()), createBlock(def));
            BLOCKS.add(block);
            blocksById.put(def.id(), block);
        }

        for (final ContentDescriptor.Entry entry : descriptor.items()) {
            final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.parse(entry.id()));
            final Item.Properties properties = new Item.Properties().setId(key).stacksTo(entry.stack());
            if (entry.rarity() != null) {
                properties.rarity(Rarity.valueOf(entry.rarity().toUpperCase(Locale.ROOT)));
            }
            final Block block = blocksById.get(entry.id());
            final Item item;
            if (block != null) {
                final BlockItem blockItem = new BlockItem(block, properties.useBlockDescriptionPrefix());
                blockItem.registerBlocks(Item.BY_BLOCK, blockItem);
                item = blockItem;
            } else {
                item = new Item(properties);
            }
            ITEMS.add(Registry.register(BuiltInRegistries.ITEM, key, item));
        }
    }

    public static ContentDescriptor descriptor() {
        return descriptor;
    }

    public static List<NewBlock> blocks() {
        return BLOCKS;
    }

    private static ResourceKey<Block> blockKey(final String id) {
        return ResourceKey.create(Registries.BLOCK, Identifier.parse(id));
    }

    private static NewBlock createBlock(final ContentDescriptor.BlockDef def) {
        final List<Property<?>> properties = new ArrayList<>();
        def.properties().forEach((name, values) -> properties.add(StringProperty.of(name, values)));
        final BlockState[] fallbacks = new BlockState[def.states()];
        for (int i = 0; i < fallbacks.length; i++) {
            fallbacks[i] = Block.stateById(descriptor.fallback()[def.firstIndex() + i]);
        }
        final NewBlock.Layout layout = new NewBlock.Layout(properties, def.properties(), fallbacks);

        // Lo que es del bloque entero sale del sustituto del primer estado; lo de cada estado, del suyo.
        // Si hay un bloque nativo del mismo material (losa de lana -> lana), dureza/sonido/color salen de el
        final BlockState fallback = fallbacks[0];
        final Block fallbackBlock = fallback.getBlock();
        final Block material = materialBlock(def.id());
        final Block hardness = material != null ? material : fallbackBlock;
        final BlockBehaviour.Properties props = BlockBehaviour.Properties.of()
            .setId(blockKey(def.id()))
            .destroyTime(hardness.defaultDestroyTime())
            .explosionResistance(hardness.getExplosionResistance())
            .friction(fallbackBlock.getFriction())
            .speedFactor(fallbackBlock.getSpeedFactor())
            .jumpFactor(fallbackBlock.getJumpFactor())
            .sound(soundType(def.id(), material != null ? material.defaultBlockState().getSoundType() : fallback.getSoundType()))
            .pushReaction(fallback.getPistonPushReaction())
            .offsetType(offsetType(fallback))
            .mapColor(state -> (material != null ? material.defaultBlockState() : layout.fallback(state))
                .getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO))
            .lightLevel(state -> layout.fallback(state).getLightEmission())
            .isRedstoneConductor((state, level, pos) -> layout.fallback(state).isRedstoneConductor(level, pos))
            .isSuffocating((state, level, pos) -> layout.fallback(state).isSuffocating(level, pos))
            .isViewBlocking((state, level, pos) -> layout.fallback(state).isViewBlocking(level, pos));
        if ((material != null ? material.defaultBlockState() : fallback).requiresCorrectToolForDrops()) {
            props.requiresCorrectToolForDrops();
        }
        if (!fallback.canOcclude()) {
            props.noOcclusion();
        }
        if (fallback.canBeReplaced()) {
            props.replaceable();
        }
        if (fallback.ignitedByLava()) {
            props.ignitedByLava();
        }
        if (((BlockBehaviourAccessor) fallbackBlock).fnviaviewer$hasDynamicShape()) {
            props.dynamicShape();
        }

        final NewBlock block = NewBlock.create(layout, props, def.defaults());
        // El orden de estados tiene que ser el de la version nueva: si no, el sustituto y el ID no cuadran
        final List<BlockState> states = block.getStateDefinition().getPossibleStates();
        for (int i = 0; i < states.size(); i++) {
            if (layout.index(states.get(i)) != i || states.size() != def.states()) {
                throw new IllegalStateException(def.id() + ": orden de estados distinto al de " + descriptor.version());
            }
        }
        return block;
    }

    private static final Pattern MATERIAL_VARIANT = Pattern.compile("(.+_(?:wool|concrete))_(?:slab|stairs)");

    /** Bloque nativo del mismo material para las variantes que ViaBackwards cambia por otro material. */
    private static Block materialBlock(final String id) {
        final Matcher matcher = MATERIAL_VARIANT.matcher(id);
        if (!matcher.matches()) {
            return null;
        }
        return BuiltInRegistries.BLOCK.getOptional(Identifier.parse(matcher.group(1))).orElse(null);
    }

    /**
     * Sonidos propios de la version nueva (block.<material>.break...) buscando el material mas largo
     * contenido en el nombre: cinnabar_brick_stairs -> cinnabar, orange_poplar_leaves -> poplar_leaves.
     * Lo que falte sale del tipo de sonido nativo.
     */
    private static SoundType soundType(final String id, final SoundType base) {
        final String name = Identifier.parse(id).getPath();
        String best = null;
        for (final ContentDescriptor.Entry sound : descriptor.sounds()) {
            final String path = Identifier.parse(sound.id()).getPath();
            if (path.startsWith("block.") && path.endsWith(".break")) {
                final String group = path.substring("block.".length(), path.length() - ".break".length());
                if (("_" + name + "_").contains("_" + group + "_") && (best == null || group.length() > best.length())) {
                    best = group;
                }
            }
        }
        if (best == null) {
            return base;
        }
        return new SoundType(base.getVolume(), base.getPitch(), sound(best, "break", base.getBreakSound()),
            sound(best, "step", base.getStepSound()), sound(best, "place", base.getPlaceSound()),
            sound(best, "hit", base.getHitSound()), sound(best, "fall", base.getFallSound()));
    }

    private static SoundEvent sound(final String group, final String kind, final SoundEvent fallback) {
        return BuiltInRegistries.SOUND_EVENT.getOptional(Identifier.withDefaultNamespace("block." + group + "." + kind)).orElse(fallback);
    }

    /** No hay getter del tipo de desplazamiento: se deduce probando posiciones. */
    private static BlockBehaviour.OffsetType offsetType(final BlockState state) {
        if (!state.hasOffsetFunction()) {
            return BlockBehaviour.OffsetType.NONE;
        }
        for (int i = 1; i < 16; i++) {
            if (state.getOffset(new BlockPos(i * 7, 0, i * 13)).y != 0) {
                return BlockBehaviour.OffsetType.XYZ;
            }
        }
        return BlockBehaviour.OffsetType.XZ;
    }

    /** IDs de todo lo nuevo en este cliente, por indice D, para el ultimo paso de Via. */
    public static MappingPatcher.Client clientIds() {
        final int[] states = new int[descriptor.stateCount()];
        final int[] blocks = new int[BLOCKS.size()];
        for (int b = 0; b < BLOCKS.size(); b++) {
            final NewBlock block = BLOCKS.get(b);
            final int first = descriptor.blocks().get(b).firstIndex();
            for (final BlockState state : block.getStateDefinition().getPossibleStates()) {
                states[first + block.layout().index(state)] = Block.BLOCK_STATE_REGISTRY.getId(state);
            }
            blocks[b] = BuiltInRegistries.BLOCK.getId(block);
        }
        final int[] items = ITEMS.stream().mapToInt(BuiltInRegistries.ITEM::getId).toArray();
        final int[] sounds = SOUNDS.stream().mapToInt(BuiltInRegistries.SOUND_EVENT::getId).toArray();
        for (final int[] ids : List.of(states, blocks, items, sounds)) {
            for (final int id : ids) {
                if (id < 0) {
                    throw new IllegalStateException("Contenido nuevo sin ID en el cliente");
                }
            }
        }
        return new MappingPatcher.Client(
            new MappingPatcher.ClientIds(states, Block.BLOCK_STATE_REGISTRY.size()),
            new MappingPatcher.ClientIds(blocks, BuiltInRegistries.BLOCK.size()),
            new MappingPatcher.ClientIds(items, BuiltInRegistries.ITEM.size()),
            new MappingPatcher.ClientIds(sounds, BuiltInRegistries.SOUND_EVENT.size()));
    }
}
