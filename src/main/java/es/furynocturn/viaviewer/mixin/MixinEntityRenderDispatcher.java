package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.entity.NewEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Entidades de 26.3 con su renderer propio, aunque en la red sean el tipo sustituto. */
@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {

    @Inject(method = "getRenderer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/client/renderer/entity/EntityRenderer;",
        at = @At("HEAD"), cancellable = true)
    private void fnviaviewer$newEntity(Entity entity, CallbackInfoReturnable<EntityRenderer<?, ?>> cir) {
        final EntityRenderer<?, ?> renderer = NewEntityRenderers.forEntity(entity);
        if (renderer != null) {
            cir.setReturnValue(renderer);
        }
    }

    @Inject(method = "getRenderer(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/client/renderer/entity/EntityRenderer;",
        at = @At("HEAD"), cancellable = true)
    private void fnviaviewer$newEntityState(EntityRenderState state, CallbackInfoReturnable<EntityRenderer<?, ?>> cir) {
        if (state instanceof NewEntityRenderers.Marked marked) {
            cir.setReturnValue(marked.renderer());
        }
    }
}
