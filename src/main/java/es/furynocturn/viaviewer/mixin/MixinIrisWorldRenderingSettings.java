package es.furynocturn.viaviewer.mixin;

import es.furynocturn.viaviewer.content.NewBlock;
import es.furynocturn.viaviewer.content.NewContent;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Iris (opcional): los shaderpacks de la version nativa no conocen los bloques nuevos en su
 * block.properties. Cada estado nuevo que el pack no nombre usa el ID de shader de su sustituto
 * (hojas que ondean, emisivos, agua...). Si el pack ya lo nombra, se respeta.
 */
@Pseudo
@Mixin(targets = "net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings", remap = false)
public abstract class MixinIrisWorldRenderingSettings {

    @Inject(method = "setBlockStateIds", at = @At("HEAD"), remap = false, require = 0)
    private void fnviaviewer$aliasNewBlocks(Object2IntMap<BlockState> ids, CallbackInfo ci) {
        if (ids == null) {
            return;
        }
        for (final NewBlock block : NewContent.blocks()) {
            for (final BlockState state : block.getStateDefinition().getPossibleStates()) {
                final BlockState fallback = block.layout().fallback(state);
                if (!ids.containsKey(state) && ids.containsKey(fallback)) {
                    ids.put(state, ids.getInt(fallback));
                }
            }
        }
    }
}
