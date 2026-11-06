package es.furynocturn.viaviewer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Sulfur cube de 26.3 sobre el slime que pone ViaBackwards: capa exterior translucida, capa interior
 * (o el bloque que lleva tragado en su lugar), tamano de cria y la mecha que lo hincha y lo hace
 * parpadear como la TNT. Medidas, texturas y transformaciones copiadas de SulfurCubeRenderer de 26.3.
 */
final class SulfurCubeRenderer extends MobRenderer<Slime, SulfurCubeRenderer.State, SulfurCubeRenderer.CubeModel> {

    static final ModelLayerLocation OUTER = layer("main");
    static final ModelLayerLocation INNER = layer("inner");
    static final ModelLayerLocation SMALL_OUTER = layer("small");
    static final ModelLayerLocation SMALL_INNER = layer("small_inner");

    private static final Identifier OUTER_TEXTURE = texture("sulfur_cube_outer");
    private static final Identifier SMALL_OUTER_TEXTURE = texture("sulfur_cube_outer_small");
    private static final Identifier INNER_TEXTURE = texture("sulfur_cube_inner");
    private static final Identifier SMALL_INNER_TEXTURE = texture("sulfur_cube_inner_small");

    private final CubeModel normalModel;
    private final CubeModel smallModel;

    SulfurCubeRenderer(final EntityRendererProvider.Context context) {
        super(context, new CubeModel(context.bakeLayer(OUTER)), 0.25F);
        this.normalModel = this.model;
        this.smallModel = new CubeModel(context.bakeLayer(SMALL_OUTER));
        this.addLayer(new InnerLayer(this, new CubeModel(context.bakeLayer(INNER)), new CubeModel(context.bakeLayer(SMALL_INNER))));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getPlayerSkinRenderCache(),
            new CustomHeadLayer.Transforms(0.0F, 0.625F, 0.84210527F)));
    }

    private static ModelLayerLocation layer(final String name) {
        return new ModelLayerLocation(Identifier.withDefaultNamespace("sulfur_cube"), name);
    }

    private static Identifier texture(final String name) {
        return Identifier.withDefaultNamespace("textures/entity/sulfur_cube/" + name + ".png");
    }

    static LayerDefinition cube(final int texV, final float half, final int textureSize) {
        final MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("cube",
            CubeListBuilder.create().texOffs(0, texV).addBox(-half, -half, -half, half * 2, half * 2, half * 2), PartPose.ZERO);
        return LayerDefinition.create(mesh, textureSize, textureSize);
    }

    @Override
    public State createRenderState() {
        return new State(this);
    }

    @Override
    public void extractRenderState(final Slime entity, final State state, final float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.squish = Mth.lerp(partialTick, entity.oSquish, entity.squish);
        state.size = entity.getSize();
        state.isBaby = NewEntities.isBabyCube(entity);
        state.fuse = NewEntities.cubeFuse(entity, partialTick);
        state.containedBlock = null;
        final ItemStack contained = entity.getItemBySlot(EquipmentSlot.BODY);
        if (!contained.isEmpty() && contained.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof AbstractSkullBlock skull) {
                state.wornHeadType = skull.getType();
                state.wornHeadProfile = contained.get(DataComponents.PROFILE);
            } else {
                state.containedBlock = contained.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY)
                    .apply(Block.byItem(blockItem).defaultBlockState());
            }
        }
        if (NewEntities.hasReplacementName(entity, "sulfur_cube")) {
            state.nameTag = null;
        }
    }

    @Override
    public void submit(final State state, final PoseStack poseStack, final SubmitNodeCollector collector, final CameraRenderState camera) {
        this.model = state.isBaby ? this.smallModel : this.normalModel;
        super.submit(state, poseStack, collector, camera);
    }

    @Override
    protected float getShadowRadius(final State state) {
        return state.size * 0.25F;
    }

    @Override
    protected void scale(final State state, final PoseStack poseStack) {
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(0.0F, 0.001F, 0.0F);
        // Con un bloque dentro no se aplasta al botar
        final float size = state.size;
        final float squish = state.containedBlock == null ? state.squish / (size * 0.5F + 1.0F) : 0.0F;
        final float width = 1.0F / (squish + 1.0F);
        poseStack.scale(width * size, 1.0F / width * size, width * size);
        if (state.fuse > 0.0F && state.fuse < 10.0F) {
            final float swell = 1.0F + swell(state.fuse);
            poseStack.scale(swell, swell, swell);
        }
        final float downscale = state.isBaby ? 1.0F : 0.5F;
        poseStack.scale(downscale, downscale, downscale);
        poseStack.translate(0.0F, (state.isBaby ? 1.24F : 0.98F) - (state.isInvisible ? 0.0F : 1.0F) / 16.0F, 0.0F);
    }

    /** TntRenderer.getSwellAmount de 26.3. */
    private static float swell(final float fuse) {
        float g = Mth.clamp(1.0F - fuse / 10.0F, 0.0F, 1.0F);
        g *= g;
        g *= g;
        return g * 0.3F;
    }

    /** TntRenderer.isLit de 26.3: parpadea cada 5 ticks. */
    private static boolean lit(final float fuse) {
        return fuse >= 0.0F && (int) (fuse / 5.0F) % 2 == 0;
    }

    @Override
    public Identifier getTextureLocation(final State state) {
        return state.isBaby ? SMALL_OUTER_TEXTURE : OUTER_TEXTURE;
    }

    static final class State extends SlimeRenderState implements NewEntityRenderers.Marked {
        private final SulfurCubeRenderer renderer;
        float fuse = -1.0F;
        @Nullable BlockState containedBlock;

        State(final SulfurCubeRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public EntityRenderer<?, ?> renderer() {
            return this.renderer;
        }
    }

    static final class CubeModel extends EntityModel<State> implements HeadedModel {
        CubeModel(final ModelPart root) {
            super(root, RenderTypes::entityTranslucent);
        }

        @Override
        public ModelPart getHead() {
            return this.root().getChild("cube");
        }
    }

    /** El nucleo del cubo, o el bloque tragado girado y centrado en su lugar. Con una cabeza dentro no se dibuja nada (lo hace CustomHeadLayer). */
    static final class InnerLayer extends RenderLayer<State, CubeModel> {
        private final CubeModel normalModel;
        private final CubeModel smallModel;

        InnerLayer(final SulfurCubeRenderer renderer, final CubeModel normalModel, final CubeModel smallModel) {
            super(renderer);
            this.normalModel = normalModel;
            this.smallModel = smallModel;
        }

        @Override
        public void submit(final PoseStack poseStack, final SubmitNodeCollector collector, final int light, final State state,
                           final float yRot, final float xRot) {
            if (state.wornHeadType != null) {
                return;
            }
            final int overlay = state.fuse > 0.0F && lit(state.fuse)
                ? OverlayTexture.pack(OverlayTexture.u(1.0F), 10)
                : LivingEntityRenderer.getOverlayCoords(state, 0.0F);
            if (state.containedBlock != null) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
                if (state.isBaby) {
                    poseStack.scale(0.5F, 0.5F, 0.5F);
                }
                poseStack.translate(-0.5F, -0.518F, -0.5F);
                collector.submitBlock(poseStack, state.containedBlock, state.lightCoords, overlay, state.outlineColor);
                poseStack.popPose();
            } else if (!state.isInvisible) {
                collector.order(-1).submitModel(state.isBaby ? this.smallModel : this.normalModel, state, poseStack,
                    RenderTypes.entityTranslucent(state.isBaby ? SMALL_INNER_TEXTURE : INNER_TEXTURE), light, overlay, state.outlineColor, null);
            }
        }
    }
}
