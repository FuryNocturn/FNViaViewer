package es.furynocturn.viaviewer.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.class)
public interface BlockBehaviourAccessor {

    @Accessor("dynamicShape")
    boolean fnviaviewer$hasDynamicShape();
}
