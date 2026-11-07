package es.furynocturn.viaviewer.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import es.furynocturn.viaviewer.content.NewContent;
import net.minecraft.client.model.Model;
//#if MC >= 12109
import net.minecraft.client.renderer.SubmitNodeCollector;
//#else
//$$ import net.minecraft.client.renderer.MultiBufferSource;
//#endif
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
//#if MC >= 12109
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
//#endif
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * En 26.2+ la tabla del cartel es el modelo del bloque (ya lo dibuja la cache de 26.3): para el alamo
 * el renderer vanilla solo pone el texto. Sin esto saldria la tabla dos veces, una sin textura.
 */
@Mixin(AbstractSignRenderer.class)
public abstract class MixinAbstractSignRenderer {

    //#if MC >= 12109
    @Inject(method = "submitSign", at = @At("HEAD"), cancellable = true)
    private void fnviaviewer$skipModelBoard(PoseStack poseStack, int light, WoodType woodType, Model.Simple model,
                                            ModelFeatureRenderer.CrumblingOverlay crumbling, SubmitNodeCollector collector,
                                            CallbackInfo ci) {
    //#else
    //$$ @Inject(method = "renderSign", at = @At("HEAD"), cancellable = true)
    //$$ private void fnviaviewer$skipModelBoard(PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, WoodType woodType,
    //$$                                         Model model, CallbackInfo ci) {
    //#endif
        if (NewContent.isPoplar(woodType)) {
            ci.cancel();
        }
    }
}
