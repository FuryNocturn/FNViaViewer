package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.entity.NewEntityRenderers;
import es.furynocturn.viaviewer.entity.baby.BabyRenderers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Entidades de 26.3 con su renderer propio, aunque en la red sean el tipo sustituto, y crias con su aspecto de 26.x. */
@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {

    @Inject(method = "getRenderer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/client/renderer/entity/EntityRenderer;",
        at = @At("HEAD"), cancellable = true)
    private void fnviaviewer$newEntity(Entity entity, CallbackInfoReturnable<EntityRenderer<?, ?>> cir) {
        EntityRenderer<?, ?> renderer = NewEntityRenderers.forEntity(entity);
        if (renderer == null) {
            renderer = BabyRenderers.forEntity(entity);
        }
        if (renderer != null) {
            cir.setReturnValue(renderer);
        }
    }

    @Inject(method = "getRenderer(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/client/renderer/entity/EntityRenderer;",
        at = @At("HEAD"), cancellable = true)
    private void fnviaviewer$newEntityState(EntityRenderState state, CallbackInfoReturnable<EntityRenderer<?, ?>> cir) {
        if (state instanceof NewEntityRenderers.Marked marked) {
            cir.setReturnValue(marked.renderer());
            return;
        }
        final EntityRenderer<?, ?> baby = BabyRenderers.forState(state);
        if (baby != null) {
            cir.setReturnValue(baby);
        }
    }
}
