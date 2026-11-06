package es.furynocturn.viaviewer.mixin;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.AtlasManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRendererProvider.Context.class)
public interface EntityRendererContextAccessor {

    @Accessor("atlasManager")
    AtlasManager fnviaviewer$atlasManager();
}
