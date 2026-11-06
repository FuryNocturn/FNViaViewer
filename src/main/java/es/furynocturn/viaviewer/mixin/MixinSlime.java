package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.config.ViewerConfig;
import es.furynocturn.viaviewer.content.NewParticles;
import es.furynocturn.viaviewer.entity.NewEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** El slime que sustituye al sulfur cube suelta sus pegotes al botar (las particulas salen en el cliente). */
@Mixin(Slime.class)
public abstract class MixinSlime {

    @Inject(method = "getParticleType", at = @At("HEAD"), cancellable = true)
    private void fnviaviewer$sulfurCubeGoo(CallbackInfoReturnable<ParticleOptions> cir) {
        if (ViewerConfig.get().particles && NewEntities.kind((Slime) (Object) this) == NewEntities.Kind.SULFUR_CUBE) {
            cir.setReturnValue(NewParticles.simple("minecraft:sulfur_cube_goo"));
        }
    }
}
