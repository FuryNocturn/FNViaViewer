package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.entity.baby.BabyRenderers;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Las texturas de entidad pasan por aqui: con una cria de 26.x dibujandose, se cambian por su version de cria. */
@Mixin(RenderTypes.class)
public abstract class MixinRenderTypes {

    @ModifyVariable(method = {
        "armorCutoutNoCull", "armorTranslucent", "entitySolid", "entitySolidZOffsetForward", "entityCutout",
        "entityCutoutNoCull(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/rendertype/RenderType;",
        "entityCutoutNoCull(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;",
        "entityCutoutNoCullZOffset(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/rendertype/RenderType;",
        "entityCutoutNoCullZOffset(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;",
        "entityTranslucent(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/rendertype/RenderType;",
        "entityTranslucent(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;",
        "entityTranslucentEmissive(Lnet/minecraft/resources/Identifier;Z)Lnet/minecraft/client/renderer/rendertype/RenderType;",
        "entityTranslucentEmissive(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;",
        "entitySmoothCutout", "entityDecal", "entityNoOutline", "eyes", "outline"
    }, at = @At("HEAD"), argsOnly = true)
    private static Identifier fnviaviewer$babyTexture(Identifier texture) {
        return BabyRenderers.texture(texture);
    }
}
