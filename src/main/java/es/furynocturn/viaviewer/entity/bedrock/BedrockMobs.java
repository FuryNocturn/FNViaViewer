package es.furynocturn.viaviewer.entity.bedrock;

import com.mojang.blaze3d.vertex.PoseStack;
import es.furynocturn.viaviewer.assets.AssetCache;
import es.furynocturn.viaviewer.entity.NewEntities;
import es.furynocturn.viaviewer.entity.NewEntityRenderers;
import es.furynocturn.viaviewer.via.ViaTranslator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
//#if MC >= 12109
import net.minecraft.client.renderer.SubmitNodeCollector;
//#else
//$$ import net.minecraft.client.renderer.MultiBufferSource;
//#endif
import net.minecraft.client.renderer.entity.CamelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
//#if MC >= 12109
import net.minecraft.client.renderer.state.CameraRenderState;
//#endif
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.squid.GlowSquid;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import org.jspecify.annotations.Nullable;

/**
 * Mobs de 1.21.9-1.21.11 en clientes que no los tienen. En la red siguen siendo el sustituto de
 * ViaBackwards (nautilus -> calamar, zombie nautilus -> calamar brillante, camel husk -> camello,
 * parched -> esqueleto, golem de cobre -> rana); se dibujan con su forma real:
 * <ul>
 *   <li>camel husk y parched: el modelo vanilla del sustituto con la textura de 26.3 (el parched, con la
 *   geometria de Bedrock, que es la de 26.3: su textura es de 64x64);</li>
 *   <li>nautilus y golem de cobre: geometria y animaciones de Bedrock (bedrock-samples, en la cache),
 *   con las texturas Java de 26.3, que tienen la misma distribucion.</li>
 * </ul>
 * Si los ficheros de Bedrock aun no se han bajado, ese mob se queda con el sustituto hasta la recarga.
 */
public final class BedrockMobs {

    private static final String VERSION = "26.3";

    private BedrockMobs() {
    }

    /** Se rehacen con cada recarga de recursos, junto a los vanilla. */
    public static void create(final EntityRendererProvider.Context context, final Map<NewEntities.Kind, EntityRenderer<?, ?>> renderers) {
        renderers.put(NewEntities.Kind.CAMEL_HUSK, new CamelHuskRenderer(context));
        add(renderers, NewEntities.Kind.PARCHED, () -> new ParchedRenderer(context, BedrockGeometry.load(file("parched.geo.json"),
            "geometry.parched", Map.of("head", "head", "body", "body", "rightArm", "right_arm", "leftArm", "left_arm",
                "rightLeg", "right_leg", "leftLeg", "left_leg"), true, List.of("head/hat"))));
        add(renderers, NewEntities.Kind.NAUTILUS, () -> new NautilusRenderer(context, false));
        add(renderers, NewEntities.Kind.ZOMBIE_NAUTILUS, () -> new NautilusRenderer(context, true));
        // El golem de cobre es vanilla desde 1.21.9
        //#if MC < 12109
        //$$ add(renderers, NewEntities.Kind.COPPER_GOLEM, () -> new CopperGolemRenderer(context));
        //#endif
    }

    /** El sustituto que ViaBackwards pone a cada mob: con otra clase (el mob ya es vanilla en este cliente) no se toca. */
    public static boolean isReplacement(final NewEntities.Kind kind, final Entity entity) {
        return switch (kind) {
            case NAUTILUS -> entity.getClass() == Squid.class;
            case ZOMBIE_NAUTILUS -> entity.getClass() == GlowSquid.class;
            case CAMEL_HUSK -> entity.getClass() == Camel.class;
            case PARCHED -> entity.getClass() == Skeleton.class;
            case COPPER_GOLEM -> entity.getClass() == Frog.class;
            default -> true;
        };
    }

    private interface Factory {
        EntityRenderer<?, ?> create() throws IOException;
    }

    private static void add(final Map<NewEntities.Kind, EntityRenderer<?, ?>> renderers, final NewEntities.Kind kind, final Factory factory) {
        try {
            renderers.put(kind, factory.create());
        } catch (final IOException | RuntimeException e) {
            ViaTranslator.LOGGER.warn("{} sin modelo de Bedrock todavia: se ve el sustituto ({})", kind, e.toString());
        }
    }

    private static Path file(final String name) throws IOException {
        final Path path = AssetCache.bedrockFile(VERSION, name);
        if (!Files.exists(path)) {
            throw new IOException("falta " + name);
        }
        return path;
    }

    private static Identifier texture(final String path) {
        return Identifier.withDefaultNamespace("textures/entity/" + path + ".png");
    }

    private static @Nullable KeyframeAnimation bake(final Map<String, AnimationDefinition> animations, final String name, final ModelPart root) {
        final AnimationDefinition definition = animations.get(name);
        return definition != null ? definition.bake(root) : null;
    }

    private static void hideReplacementName(final Entity entity, final LivingEntityRenderState state, final String type) {
        if (NewEntities.hasReplacementName(entity, type)) {
            state.nameTag = null;
        }
    }

    // ---------------------------------------------------------------- camel husk

    static final class CamelHuskRenderer extends CamelRenderer {
        private static final Identifier TEXTURE = texture("camel/camel_husk");

        CamelHuskRenderer(final EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public Identifier getTextureLocation(final CamelRenderState state) {
            return TEXTURE;
        }

        @Override
        public CamelRenderState createRenderState() {
            final CamelHuskRenderer self = this;
            final class State extends CamelRenderState implements NewEntityRenderers.Marked {
                @Override
                public EntityRenderer<?, ?> renderer() {
                    return self;
                }
            }
            return new State();
        }

        @Override
        public void extractRenderState(final Camel entity, final CamelRenderState state, final float partialTick) {
            super.extractRenderState(entity, state, partialTick);
            hideReplacementName(entity, state, "camel_husk");
        }
    }

    // ---------------------------------------------------------------- parched

    static final class ParchedRenderer extends SkeletonRenderer {
        private static final Identifier TEXTURE = texture("skeleton/parched");

        ParchedRenderer(final EntityRendererProvider.Context context, final LayerDefinition layer) {
            super(context);
            this.model = new SkeletonModel<>(layer.bakeRoot());
        }

        @Override
        public Identifier getTextureLocation(final SkeletonRenderState state) {
            return TEXTURE;
        }

        @Override
        public SkeletonRenderState createRenderState() {
            final ParchedRenderer self = this;
            final class State extends SkeletonRenderState implements NewEntityRenderers.Marked {
                @Override
                public EntityRenderer<?, ?> renderer() {
                    return self;
                }
            }
            return new State();
        }

        @Override
        public void extractRenderState(final Skeleton entity, final SkeletonRenderState state, final float partialTick) {
            super.extractRenderState(entity, state, partialTick);
            hideReplacementName(entity, state, "parched");
        }
    }

    // ---------------------------------------------------------------- nautilus

    static final class NautilusState extends LivingEntityRenderState implements NewEntityRenderers.Marked {
        private final NautilusRenderer renderer;
        boolean warm;

        NautilusState(final NautilusRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public EntityRenderer<?, ?> renderer() {
            return this.renderer;
        }
    }

    /** Como NautilusModel de Java: el cuerpo sigue la mirada (hasta 10 grados) y respira/nada con las animaciones de Bedrock. */
    static final class NautilusModel extends EntityModel<NautilusState> {
        private final ModelPart body;
        private final @Nullable KeyframeAnimation breathe;
        private final @Nullable KeyframeAnimation swim;

        NautilusModel(final ModelPart root, final Map<String, AnimationDefinition> animations) {
            super(root);
            // El hueso de fuera se llama nautilus (adulto, coral) o nautilus_baby
            this.body = root.getChild(root.hasChild("nautilus") ? "nautilus" : "nautilus_baby").getChild("body");
            this.breathe = bake(animations, "animation.nautilus.breathe", root);
            this.swim = bake(animations, "animation.nautilus.swim", root);
        }

        @Override
        public void setupAnim(final NautilusState state) {
            super.setupAnim(state);
            this.body.yRot = Mth.clamp(state.yRot, -10.0F, 10.0F) * Mth.DEG_TO_RAD;
            this.body.xRot = Mth.clamp(state.xRot, -10.0F, 10.0F) * Mth.DEG_TO_RAD;
            if (this.breathe != null) {
                this.breathe.applyWalk(state.walkAnimationPos + state.ageInTicks / 5.0F, state.walkAnimationSpeed + 0.2F, 2.0F, 3.0F);
            }
            if (this.swim != null) {
                this.swim.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2.0F, 3.0F);
            }
        }
    }

    static final class NautilusRenderer extends MobRenderer<Squid, NautilusState, NautilusModel> {
        private final boolean zombie;
        private final NautilusModel adult;
        private final NautilusModel baby;
        private final NautilusModel coral;

        NautilusRenderer(final EntityRendererProvider.Context context, final boolean zombie) throws IOException {
            this(context, zombie, BedrockAnimations.load(file("nautilus.animation.json")));
        }

        private NautilusRenderer(final EntityRendererProvider.Context context, final boolean zombie,
                                 final Map<String, AnimationDefinition> animations) throws IOException {
            super(context, model(file("nautilus.geo.json"), "geometry.nautilus", animations), 0.7F);
            this.zombie = zombie;
            this.adult = this.model;
            this.baby = model(file("nautilus_baby.geo.json"), "geometry.nautilus.baby", animations);
            this.coral = model(file("zombie_nautilus_coral.geo.json"), "geometry.zombie_nautilus_coral", animations);
        }

        private static NautilusModel model(final Path file, final String geometry, final Map<String, AnimationDefinition> animations)
            throws IOException {
            return new NautilusModel(BedrockGeometry.load(file, geometry, Map.of(), false, List.of()).bakeRoot(), animations);
        }

        @Override
        public NautilusState createRenderState() {
            return new NautilusState(this);
        }

        @Override
        public void extractRenderState(final Squid entity, final NautilusState state, final float partialTick) {
            super.extractRenderState(entity, state, partialTick);
            state.warm = this.zombie && NewEntities.warmZombieNautilus(entity);
            hideReplacementName(entity, state, this.zombie ? "zombie_nautilus" : "nautilus");
        }

        private NautilusModel pick(final NautilusState state) {
            return state.warm ? this.coral : state.isBaby && !this.zombie ? this.baby : this.adult;
        }

        //#if MC >= 12109
        @Override
        public void submit(final NautilusState state, final PoseStack poseStack, final SubmitNodeCollector collector, final CameraRenderState camera) {
            this.model = this.pick(state);
            super.submit(state, poseStack, collector, camera);
        }
        //#else
        //$$ @Override
        //$$ public void render(final NautilusState state, final PoseStack poseStack, final MultiBufferSource buffers, final int light) {
        //$$     this.model = this.pick(state);
        //$$     super.render(state, poseStack, buffers, light);
        //$$ }
        //#endif

        @Override
        public Identifier getTextureLocation(final NautilusState state) {
            if (this.zombie) {
                return texture(state.warm ? "nautilus/zombie_nautilus_coral" : "nautilus/zombie_nautilus");
            }
            return texture(state.isBaby ? "nautilus/nautilus_baby" : "nautilus/nautilus");
        }
    }

    // ---------------------------------------------------------------- golem de cobre

    private static final String[] OXIDATION = {"", "_exposed", "_weathered", "_oxidized"};

    static final class GolemState extends LivingEntityRenderState implements NewEntityRenderers.Marked {
        private final CopperGolemRenderer renderer;
        int weathering;

        GolemState(final CopperGolemRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public EntityRenderer<?, ?> renderer() {
            return this.renderer;
        }
    }

    /** Como CopperGolemModel de Java: la cabeza sigue la mirada y anda con la animacion de Bedrock. */
    static final class GolemModel extends EntityModel<GolemState> {
        private final ModelPart head;
        private final @Nullable KeyframeAnimation walk;

        GolemModel(final ModelPart root, final Map<String, AnimationDefinition> animations) {
            super(root);
            this.head = root.getChild("root").getChild("body").getChild("head");
            this.walk = bake(animations, "animation.copper_golem.walk", root);
        }

        @Override
        public void setupAnim(final GolemState state) {
            super.setupAnim(state);
            this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
            this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
            if (this.walk != null) {
                this.walk.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2.0F, 2.5F);
            }
        }
    }

    static final class CopperGolemRenderer extends MobRenderer<Frog, GolemState, GolemModel> {

        CopperGolemRenderer(final EntityRendererProvider.Context context) throws IOException {
            super(context, new GolemModel(BedrockGeometry.load(file("copper_golem.geo.json"), "geometry.copper_golem", Map.of(), false,
                List.of()).bakeRoot(), BedrockAnimations.load(file("copper_golem.animation.json"))), 0.5F);
            this.addLayer(new Eyes(this));
        }

        @Override
        public GolemState createRenderState() {
            return new GolemState(this);
        }

        @Override
        public void extractRenderState(final Frog entity, final GolemState state, final float partialTick) {
            super.extractRenderState(entity, state, partialTick);
            state.weathering = Mth.clamp(NewEntities.golemWeathering(entity), 0, OXIDATION.length - 1);
            hideReplacementName(entity, state, "copper_golem");
        }

        @Override
        public Identifier getTextureLocation(final GolemState state) {
            return texture("copper_golem/copper_golem" + OXIDATION[state.weathering]);
        }
    }

    /** Los ojos del golem, que brillan en la oscuridad (LivingEntityEmissiveLayer en Java). */
    static final class Eyes extends RenderLayer<GolemState, GolemModel> {
        Eyes(final CopperGolemRenderer renderer) {
            super(renderer);
        }

        private static Identifier eyes(final GolemState state) {
            return texture("copper_golem/copper_golem_eyes" + OXIDATION[state.weathering]);
        }

        //#if MC >= 12109
        @Override
        public void submit(final PoseStack poseStack, final SubmitNodeCollector collector, final int light, final GolemState state,
                           final float yRot, final float xRot) {
            if (!state.isInvisible) {
                collector.submitModel(this.getParentModel(), state, poseStack, RenderTypes.eyes(eyes(state)), light,
                    OverlayTexture.NO_OVERLAY, state.outlineColor, null);
            }
        }
        //#else
        //$$ @Override
        //$$ public void render(final PoseStack poseStack, final MultiBufferSource buffers, final int light, final GolemState state,
        //$$                    final float yRot, final float xRot) {
        //$$     if (!state.isInvisible) {
        //$$         this.getParentModel().renderToBuffer(poseStack, buffers.getBuffer(RenderType.eyes(eyes(state))), light, OverlayTexture.NO_OVERLAY);
        //$$     }
        //$$ }
        //#endif
    }
}
