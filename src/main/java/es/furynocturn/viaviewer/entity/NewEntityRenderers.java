package es.furynocturn.viaviewer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.Nullable;

/**
 * Renderers propios de las entidades de 26.3. Sus render states llevan una marca para que el
 * dispatcher los vuelva a mandar aqui y no al renderer del tipo sustituto.
 */
public final class NewEntityRenderers {

    /** Render state que sabe que renderer lo dibuja. */
    public interface Marked {
        EntityRenderer<?, ?> renderer();
    }

    private static final ModelLayerLocation POPLAR_BOAT = layer("boat/poplar");
    private static final ModelLayerLocation POPLAR_CHEST_BOAT = layer("chest_boat/poplar");
    private static final ModelLayerLocation CUSHION = layer("cushion");

    private static final Map<NewEntities.Kind, EntityRenderer<?, ?>> RENDERERS = new EnumMap<>(NewEntities.Kind.class);

    private NewEntityRenderers() {
    }

    private static ModelLayerLocation layer(final String path) {
        return new ModelLayerLocation(Identifier.withDefaultNamespace(path), "main");
    }

    public static void registerLayers() {
        EntityModelLayerRegistry.registerModelLayer(POPLAR_BOAT, BoatModel::createBoatModel);
        EntityModelLayerRegistry.registerModelLayer(POPLAR_CHEST_BOAT, BoatModel::createChestBoatModel);
        EntityModelLayerRegistry.registerModelLayer(CUSHION, CushionModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(SulfurCubeRenderer.OUTER, () -> SulfurCubeRenderer.cube(0, 9.0F, 128));
        EntityModelLayerRegistry.registerModelLayer(SulfurCubeRenderer.INNER, () -> SulfurCubeRenderer.cube(36, 8.0F, 128));
        EntityModelLayerRegistry.registerModelLayer(SulfurCubeRenderer.SMALL_OUTER, () -> SulfurCubeRenderer.cube(0, 5.0F, 64));
        EntityModelLayerRegistry.registerModelLayer(SulfurCubeRenderer.SMALL_INNER, () -> SulfurCubeRenderer.cube(20, 4.0F, 64));
    }

    /** Se rehacen con cada recarga de recursos, junto a los vanilla. */
    public static void create(final EntityRendererProvider.Context context) {
        RENDERERS.put(NewEntities.Kind.POPLAR_BOAT, new PoplarBoatRenderer(context, POPLAR_BOAT));
        RENDERERS.put(NewEntities.Kind.POPLAR_CHEST_BOAT, new PoplarBoatRenderer(context, POPLAR_CHEST_BOAT));
        RENDERERS.put(NewEntities.Kind.CUSHION, new CushionRenderer(context));
        RENDERERS.put(NewEntities.Kind.SULFUR_CUBE, new SulfurCubeRenderer(context));
    }

    public static @Nullable EntityRenderer<?, ?> forEntity(final Entity entity) {
        final NewEntities.Kind kind = NewEntities.kind(entity);
        if (kind == NewEntities.Kind.SULFUR_CUBE && !(entity instanceof Slime)) {
            return null;
        }
        return kind != null ? RENDERERS.get(kind) : null;
    }

    /** El de abedul con la textura de alamo (textures/entity/boat/poplar.png, de la cache). */
    static final class PoplarBoatRenderer extends BoatRenderer {
        PoplarBoatRenderer(final EntityRendererProvider.Context context, final ModelLayerLocation layer) {
            super(context, layer);
        }

        @Override
        public BoatRenderState createRenderState() {
            final PoplarBoatRenderer self = this;
            final class State extends BoatRenderState implements Marked {
                @Override
                public EntityRenderer<?, ?> renderer() {
                    return self;
                }
            }
            return new State();
        }
    }

    /** Cojin de 26.3: una losa de tela de 16x4x16 en el suelo, girada hacia donde mira. */
    static final class CushionModel extends EntityModel<CushionState> {
        CushionModel(final ModelPart root) {
            super(root);
        }

        static LayerDefinition createLayer() {
            final MeshDefinition mesh = new MeshDefinition();
            mesh.getRoot().addOrReplaceChild("cushion",
                CubeListBuilder.create().texOffs(0, 0).addBox(-31.0F, -4.0F, -1.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(-0.005F)),
                PartPose.offset(23.0F, 4.0F, -7.0F));
            return LayerDefinition.create(mesh, 64, 64);
        }
    }

    static final class CushionState extends EntityRenderState implements Marked {
        private final CushionRenderer renderer;
        Direction direction = Direction.NORTH;
        Identifier texture;

        CushionState(final CushionRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public EntityRenderer<?, ?> renderer() {
            return this.renderer;
        }
    }

    static final class CushionRenderer extends EntityRenderer<Entity, CushionState> {
        private static final Map<DyeColor, Identifier> TEXTURES = new EnumMap<>(DyeColor.class);

        static {
            for (final DyeColor color : DyeColor.values()) {
                TEXTURES.put(color, Identifier.withDefaultNamespace("textures/entity/cushion/" + color.getName() + "_cushion.png"));
            }
        }

        private final CushionModel model;

        CushionRenderer(final EntityRendererProvider.Context context) {
            super(context);
            this.model = new CushionModel(context.bakeLayer(CUSHION));
        }

        @Override
        public CushionState createRenderState() {
            return new CushionState(this);
        }

        @Override
        public void extractRenderState(final Entity entity, final CushionState state, final float partialTick) {
            super.extractRenderState(entity, state, partialTick);
            state.direction = Direction.fromYRot(entity.getYRot());
            state.texture = TEXTURES.get(NewEntities.cushionColor(entity));
            if (NewEntities.hasReplacementName(entity, "cushion")) {
                state.nameTag = null;
            }
        }

        @Override
        public void submit(final CushionState state, final PoseStack poseStack, final SubmitNodeCollector collector, final CameraRenderState camera) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.direction.toYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.translate(0.0, -0.25, 0.0);
            collector.submitModel(this.model, state, poseStack, this.model.renderType(state.texture), state.lightCoords,
                OverlayTexture.NO_OVERLAY, state.outlineColor, null);
            poseStack.popPose();
            super.submit(state, poseStack, collector, camera);
        }
    }
}
