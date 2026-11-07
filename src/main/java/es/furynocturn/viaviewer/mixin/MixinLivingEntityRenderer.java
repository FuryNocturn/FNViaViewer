package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.entity.baby.BabyRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Mientras dibuja la copia de un renderer con las crias de 26.x, sus texturas pasan a las de cria. */
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer {

    //#if MC >= 12109
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
        at = @At("HEAD"))
    //#else
    //$$ @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    //$$     at = @At("HEAD"))
    //#endif
    private void fnviaviewer$beginBaby(CallbackInfo ci) {
        BabyRenderers.begin(this);
    }

    //#if MC >= 12109
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
        at = @At("RETURN"))
    //#else
    //$$ @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    //$$     at = @At("RETURN"))
    //#endif
    private void fnviaviewer$endBaby(CallbackInfo ci) {
        BabyRenderers.end(this);
    }
}
