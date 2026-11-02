package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.entity.NewEntityRenderers;
import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Los renderers de las entidades de 26.3 se crean con el mismo contexto que los vanilla. */
@Mixin(EntityRenderers.class)
public abstract class MixinEntityRenderers {

    @Inject(method = "createEntityRenderers", at = @At("RETURN"))
    private static void fnviaviewer$createNewRenderers(EntityRendererProvider.Context context,
                                                       CallbackInfoReturnable<Map<EntityType<?>, EntityRenderer<?, ?>>> cir) {
        NewEntityRenderers.create(context);
    }
}
