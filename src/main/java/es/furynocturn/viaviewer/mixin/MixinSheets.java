package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.assets.SignTextures;
import es.furynocturn.viaviewer.content.NewContent;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 26.3 ya no trae textura de entidad para el cartel de alamo (la tabla es modelo de bloque), pero la
 * pantalla de editar cartel la sigue usando. SignTextures la genera en la cache; hasta que exista
 * (primera descarga, o sin internet) se usa la de abedul para que no salga negra y rosa.
 * En el mundo no se ve, porque ahi la tabla no se dibuja con esta textura (MixinAbstractSignRenderer).
 */
@Mixin(Sheets.class)
public abstract class MixinSheets {

    @Inject(method = "getSignMaterial", at = @At("HEAD"), cancellable = true)
    private static void fnviaviewer$poplarSignTexture(WoodType woodType, CallbackInfoReturnable<Material> cir) {
        if (NewContent.isPoplar(woodType) && !SignTextures.available("poplar")) {
            cir.setReturnValue(Sheets.getSignMaterial(WoodType.BIRCH));
        }
    }
}
