package es.furynocturn.viaviewer.entity.baby;

import es.furynocturn.viaviewer.config.ViewerConfig;
import es.furynocturn.viaviewer.entity.NewEntities;
//#if MC >= 12109
import es.furynocturn.viaviewer.mixin.EntityRendererContextAccessor;
//#endif
import es.furynocturn.viaviewer.mixin.EntityRenderersAccessor;
import es.furynocturn.viaviewer.mixin.ModelPartAccessor;
import es.furynocturn.viaviewer.via.ViaTranslator;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

/**
 * Crias con su aspecto de 26.x. De cada renderer vanilla que tiene cria se hace una segunda copia
 * cuyas capas de cria salen de la geometria de 26.3 (BabyLayers). Mientras esa copia dibuja, sus
 * texturas pasan a la version "_baby" de la cache. Solo se usa con el interruptor activo y la
 * conexion traducida por Via: en servidores de la version del cliente las crias son las de siempre.
 */
public final class BabyRenderers {

    private static final Map<EntityType<?>, EntityRenderer<?, ?>> RENDERERS = new HashMap<>();
    private static final Set<EntityRenderer<?, ?>> OWN = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Map<Identifier, Identifier> TEXTURES = new ConcurrentHashMap<>();
    /** Texturas cuyo nombre no sigue ninguna regla (rutas de 1.21.11 -> 26.3). */
    private static final Map<String, String> RENAMED = Map.of(
        "textures/entity/turtle/big_sea_turtle.png", "textures/entity/turtle/turtle_baby.png",
        "textures/entity/sniffer/sniffer.png", "textures/entity/sniffer/snifflet.png");
    private static int drawing;

    private BabyRenderers() {
    }

    /** Se rehacen con cada recarga de recursos, junto a los vanilla. */
    public static void create(final EntityRendererProvider.Context context) {
        RENDERERS.clear();
        OWN.clear();
        TEXTURES.clear();
        final Map<ModelLayerLocation, Supplier<LayerDefinition>> layers = BabyLayers.all();
        final BabyModelSet models = new BabyModelSet(context.getModelSet(), layers);
        //#if MC >= 12109
        final EntityRendererProvider.Context babyContext = new EntityRendererProvider.Context(context.getEntityRenderDispatcher(),
            context.getItemModelResolver(), context.getMapRenderer(), context.getBlockRenderDispatcher(), context.getResourceManager(),
            models, context.getEquipmentAssets(), ((EntityRendererContextAccessor) context).fnviaviewer$atlasManager(), context.getFont(),
            context.getPlayerSkinRenderCache());
        //#else
        //$$ final EntityRendererProvider.Context babyContext = new EntityRendererProvider.Context(context.getEntityRenderDispatcher(),
        //$$     context.getItemModelResolver(), context.getMapRenderer(), context.getBlockRenderDispatcher(), context.getResourceManager(),
        //$$     models, context.getEquipmentAssets(), context.getFont());
        //#endif
        EntityRenderersAccessor.fnviaviewer$providers().forEach((type, provider) -> {
            models.used = false;
            try {
                final EntityRenderer<?, ?> renderer = provider.create(babyContext);
                if (models.used) {
                    RENDERERS.put(type, renderer);
                    OWN.add(renderer);
                }
            } catch (final RuntimeException e) {
                // Las partes de la cria de 26.3 no le valen al modelo de 1.21.11: esa cria se queda como estaba
                ViaTranslator.LOGGER.warn("Cria de {} sin aspecto nuevo: {}", EntityType.getKey(type), e.toString());
            }
        });
        ViaTranslator.LOGGER.info("Crias con aspecto de 26.x: {}", RENDERERS.size());
    }

    private static boolean active() {
        return ViewerConfig.get().babies && NewEntities.translating();
    }

    public static @Nullable EntityRenderer<?, ?> forEntity(final Entity entity) {
        return entity instanceof LivingEntity living && living.isBaby() && active() ? RENDERERS.get(entity.getType()) : null;
    }

    public static @Nullable EntityRenderer<?, ?> forState(final EntityRenderState state) {
        return state instanceof LivingEntityRenderState living && living.isBaby && active() ? RENDERERS.get(state.entityType) : null;
    }

    /** Lo llama LivingEntityRenderer.submit al empezar y al acabar. */
    public static void begin(final Object renderer) {
        if (OWN.contains(renderer)) {
            drawing++;
        }
    }

    public static void end(final Object renderer) {
        if (OWN.contains(renderer) && drawing > 0) {
            drawing--;
        }
    }

    /** La textura de cria de 26.3 que corresponde a una de 1.21.11, si existe; si no, la misma. */
    public static Identifier texture(final Identifier texture) {
        if (drawing == 0 || !texture.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
            return texture;
        }
        return TEXTURES.computeIfAbsent(texture, BabyRenderers::findBabyTexture);
    }

    private static Identifier findBabyTexture(final Identifier texture) {
        final String path = texture.getPath();
        // La armadura de las crias sigue con su modelo viejo: su textura tambien
        if (!path.startsWith("textures/entity/") || path.startsWith("textures/entity/equipment/") || !path.endsWith(".png")) {
            return texture;
        }
        for (final String candidate : candidates(path)) {
            final Identifier id = Identifier.withDefaultNamespace(candidate);
            if (Minecraft.getInstance().getResourceManager().getResource(id).isPresent()) {
                return id;
            }
        }
        return texture;
    }

    /** Como renombra 26.x las texturas de cria: cow/temperate_cow -> cow/cow_temperate_baby, cat/red -> cat/cat_red_baby... */
    static List<String> candidates(final String path) {
        final String renamed = RENAMED.get(path);
        if (renamed != null) {
            return List.of(renamed);
        }
        final int slash = path.lastIndexOf('/');
        final String dir = path.substring(0, slash);
        final String name = path.substring(slash + 1, path.length() - 4);
        if (dir.equals("textures/entity")) {
            return List.of(dir + "/" + name + "/" + name + "_baby.png");
        }
        final String folder = dir.substring(dir.lastIndexOf('/') + 1);
        final String parent = dir.substring(0, dir.lastIndexOf('/'));
        final List<String> result = new java.util.ArrayList<>(List.of(dir + "/" + name + "_baby.png", dir + "/" + folder + "_" + name + "_baby.png"));
        // Una palabra del nombre pasa delante: temperate_cow -> cow_temperate, red_mooshroom -> mooshroom_red, snow_fox_sleep -> fox_snow_sleep
        final String[] words = name.split("_");
        for (int i = 1; i < words.length; i++) {
            final List<String> rest = new java.util.ArrayList<>(List.of(words));
            final String first = rest.remove(i);
            result.add(dir + "/" + first + "_" + String.join("_", rest) + "_baby.png");
        }
        result.add(parent + "/baby/" + name + ".png");
        return result;
    }

    /** Las capas de cria que hay en BabyLayers salen de ahi; las demas, del EntityModelSet de vanilla. */
    static final class BabyModelSet extends EntityModelSet {
        private final EntityModelSet vanilla;
        private final Map<ModelLayerLocation, Supplier<LayerDefinition>> layers;
        boolean used;

        BabyModelSet(final EntityModelSet vanilla, final Map<ModelLayerLocation, Supplier<LayerDefinition>> layers) {
            super(Map.of());
            this.vanilla = vanilla;
            this.layers = layers;
        }

        @Override
        public ModelPart bakeLayer(final ModelLayerLocation location) {
            final Supplier<LayerDefinition> layer = this.layers.get(location);
            if (layer == null) {
                return this.vanilla.bakeLayer(location);
            }
            this.used = true;
            return lenient(layer.get().bakeRoot());
        }

        /**
         * El modelo de 1.21.11 pide partes que la cria de 26.3 ya no tiene (lobo real_head, abeja antenas,
         * tortuga egg_belly...): se le dan vacias. Se dibuja la geometria de 26.3 y la animacion de 1.21.11
         * mueve las partes que coinciden.
         */
        private static ModelPart lenient(final ModelPart part) {
            final ModelPartAccessor access = (ModelPartAccessor) (Object) part;
            final Map<String, ModelPart> children = new LenientChildren(access.fnviaviewer$children());
            children.values().forEach(BabyModelSet::lenient);
            access.fnviaviewer$setChildren(children);
            return part;
        }
    }

    /** Hijos de una parte que devuelven una parte vacia (y tambien tolerante) cuando falta la pedida. */
    static final class LenientChildren extends java.util.LinkedHashMap<String, ModelPart> {
        LenientChildren(final Map<String, ModelPart> children) {
            super(children);
        }

        @Override
        public ModelPart get(final Object key) {
            final ModelPart part = super.get(key);
            return part != null ? part : BabyModelSet.lenient(new ModelPart(List.of(), Map.of()));
        }
    }
}
